package lt.markmerkk.utils

import lt.markmerkk.*
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.mvp.IDataStorage
import lt.markmerkk.mvp.UserSettings
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by mariusmerkevicius on 1/5/16. Handles synchronization with jira from other components
 */
class SyncController2(
        private val jiraInteractor: JiraInteractor,
        private val userSettings: UserSettings,
        private val issueStorage: IDataStorage<LocalIssue>,
        private val logStorage: IDataStorage<SimpleLog>,
        private val remoteMergeToolsProvider: RemoteMergeToolsProvider,
        private val lastUpdateController: LastUpdateController,
        private val dayProvider: DayProvider,
        private val uiScheduler: Scheduler
) {

    val remoteLoadListeners = mutableListOf<IRemoteLoadListener>()
    var subscription: Subscription? = null

    var isLoading = false
        set(value) {
            field = value
            remoteLoadListeners.forEach { it.onLoadChange(value) }
        }

    @PostConstruct // todo : replace with mvp attach/de methods
    fun init() { }

    @PreDestroy // todo : replace with mvp attach/de methods
    fun destroy() {
        subscription?.unsubscribe()
    }

    fun syncAll() {
        if (isLoading) {
            logger.info("Sync in progress")
            return
        }
        val syncStart = System.currentTimeMillis()
        val uploadValidator = JiraFilterSimpleLog()
        val downloadValidator = JiraFilterWorklog(
                userSettings.username,
                dayProvider.startDay(),
                dayProvider.endDay()
        )
        val issueValidator = JiraFilterIssue()
        subscription = uploadObservable(uploadValidator)
                .flatMap { downloadObservable(downloadValidator) }
                .flatMap { issueCacheObservable(issueValidator, syncStart) }
                .doOnSubscribe { isLoading = true }
                .doOnUnsubscribe { isLoading = false }
                .observeOn(uiScheduler)
                .subscribe({
                    val syncEnd = System.currentTimeMillis()
                    logger.info("Sync all success in ${syncEnd - syncStart}ms!")
                }, {
                    logger.info("Sync all error: ${it.message} / ${it.cause?.message?.substring(0, 40)}...")
                    logger.error("Sync all error data: ", it)
                    val errorMsg = it.message
                    remoteLoadListeners.forEach { it.onError(errorMsg) }
                    logStorage.notifyDataChange()
                    issueStorage.notifyDataChange()
                }, {
                    logStorage.notifyDataChange()
                    issueStorage.notifyDataChange()
                })
    }

    fun syncLogs() {
        if (isLoading) {
            logger.info("Sync in progress")
            return
        }
        val uploadValidator = JiraFilterSimpleLog()
        val downloadValidator = JiraFilterWorklog(
                userSettings.username,
                dayProvider.startDay(),
                dayProvider.endDay()
        )
        val syncStart = System.currentTimeMillis()
        subscription = uploadObservable(uploadValidator)
                .flatMap { downloadObservable(downloadValidator) }
                .doOnSubscribe { isLoading = true }
                .doOnUnsubscribe { isLoading = false }
                .observeOn(uiScheduler)
                .subscribe({
                    val syncEnd = System.currentTimeMillis()
                    logger.info("Log sync success in ${syncEnd - syncStart}ms!")
                }, {
                    logger.info("Log sync error: ${it.message} / ${it.cause?.message?.substring(0, 40)}...")
                    logger.error("Log sync error data: ", it)
                    logStorage.notifyDataChange()
                }, {
                    logStorage.notifyDataChange()
                })
    }

    fun syncIssues() {
        if (isLoading) {
            logger.info("Sync in progress")
            return
        }
        val syncStart = System.currentTimeMillis()
        val filter = JiraFilterIssue()
        subscription = issueCacheObservable(filter, syncStart)
                .doOnSubscribe { isLoading = true }
                .doOnUnsubscribe { isLoading = false }
                .observeOn(uiScheduler)
                .subscribe({
                    val syncEnd = System.currentTimeMillis()
                    logger.info("Issue sync success in ${syncEnd - syncStart}ms!")
                }, {
                    logger.info("Issue sync error: ${it.message}")
                    logger.error("Log sync error data: ", it)
                    issueStorage.notifyDataChange()
                }, {
                    issueStorage.notifyDataChange()
                })
    }

    //region Observables

    fun uploadObservable(filter: JiraFilter<SimpleLog>): Observable<*> {
        return jiraInteractor.jiraLocalWorks()
                .flatMap { Observable.from(it) }
                .flatMap {
                    val pushMerger = remoteMergeToolsProvider.logPushMerger(
                            localLog = it,
                            filter = filter
                    )
                    rx.util.async.Async.fromCallable(pushMerger, uiScheduler)
                }
                .toList()
    }

    fun downloadObservable(filter: JiraFilter<WorkLog>): Observable<*> {
        return jiraInteractor.jiraRemoteWorks(dayProvider.startDay(), dayProvider.endDay())
                .flatMap { Observable.from(it) }
                .flatMap {
                    val pullMerger = remoteMergeToolsProvider.logPullMerger(
                            it,
                            filter
                    )
                    rx.util.async.Async.fromCallable(pullMerger, uiScheduler)
                }
                .toList()
    }

    fun issueCacheObservable(filter: JiraFilter<Issue>, syncStart: Long): Observable<*> {
        val pullObservable = jiraInteractor.jiraIssues()
                .flatMap { Observable.from(it) }
                .flatMap {
                    val merger = remoteMergeToolsProvider.issuePullMerger(it, filter)
                    Observable.fromCallable(merger)
                }
        val removeOldObservable = jiraInteractor.jiraLocalIssuesOld(syncStart)
                .flatMap { Observable.from(it) }
                .map { issueStorage.delete(it) }
        return Observable.merge(pullObservable, removeOldObservable).toList()
    }

    //endregion

    //region Getters / Setters

    fun addLoadingListener(listener: IRemoteLoadListener?) {
        if (listener == null) return
        remoteLoadListeners.add(listener)
    }

    fun removeLoadingListener(listener: IRemoteLoadListener?) {
        if (listener == null) return
        remoteLoadListeners.remove(listener)
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(JiraSearchSubscriberImpl::class.java)
    }

}
