package lt.markmerkk.interactors

import lt.markmerkk.*
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.IDataStorage
import lt.markmerkk.UserSettings
import net.rcarz.jiraclient.WorkLog
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.util.async.Async

/**
 * Created by mariusmerkevicius on 1/5/16. Handles synchronization with jira from other components
 */
class SyncInteractorImpl(
        private val jiraInteractor: JiraInteractor,
        private val userSettings: UserSettings,
        private val logStorage: IDataStorage<SimpleLog>,
        private val remoteMergeToolsProvider: RemoteMergeToolsProvider,
        private val dayProvider: DayProvider,
        private val autoUpdateInteractor: AutoUpdateInteractor,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : SyncInteractor {

    val remoteLoadListeners = mutableListOf<IRemoteLoadListener>()
    var subscription: Subscription? = null

    var loading = false
        set(value) {
            field = value
            remoteLoadListeners.forEach { it.onLoadChange(value) }
        }

    override fun onAttach() { }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun stop() {
        logger.info("Stopping sync")
        subscription?.unsubscribe()
    }

    override fun syncAll() {
        if (loading) {
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
        subscription = uploadObservable(uploadValidator)
                .flatMap { downloadObservable(downloadValidator) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { loading = true }
                .doOnTerminate { loading = false }
                .subscribe({
                    val syncEnd = System.currentTimeMillis()
                    logger.info("Sync all success in ${syncEnd - syncStart}ms!")
                }, {
                    logger.info("Sync all error: ${it.message} / ${it.cause?.message?.substring(0, 40)}...")
                    logger.error("Sync all error data: ", it)
                    val errorMsg = it.message
                    remoteLoadListeners.forEach { it.onError(errorMsg) }
                    logStorage.notifyDataChange()
                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
                }, {
                    logStorage.notifyDataChange()
                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
                })
    }

    override fun syncLogs() {
        if (loading) {
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
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { loading = true }
                .doOnTerminate { loading = false }
                .subscribe({
                    val syncEnd = System.currentTimeMillis()
                    logger.info("Log sync success in ${syncEnd - syncStart}ms!")
                }, {
                    logger.info("Log sync error: ${it.message}")
                    logger.error("Log sync error data: ", it)
                    logStorage.notifyDataChange()
                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
                }, {
                    logStorage.notifyDataChange()
                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
                })
    }

    //region Observables

    fun uploadObservable(filter: JiraFilter<SimpleLog>): Observable<List<SimpleLog>> {
        return jiraInteractor.jiraLocalWorks()
                .flatMap { Observable.from(it) }
                .flatMap {
                    val pushMerger = remoteMergeToolsProvider.logPushMerger(
                            localLog = it,
                            filter = filter
                    )
                    Async.fromCallable(pushMerger, ioScheduler)
                }
                .toList()
    }

    fun downloadObservable(filter: JiraFilter<WorkLog>): Observable<List<JiraWork>> {
        return jiraInteractor.jiraRemoteWorks(dayProvider.startDay(), dayProvider.endDay())
                .flatMap { Observable.from(it) }
                .flatMap {
                    val pullMerger = remoteMergeToolsProvider.logPullMerger(
                            it,
                            filter
                    )
                    Async.fromCallable(pullMerger, ioScheduler)
                }
                .toList()
    }

    //endregion

    override fun addLoadingListener(listener: IRemoteLoadListener) {
        remoteLoadListeners.add(listener)
    }

    override fun removeLoadingListener(listener: IRemoteLoadListener) {
        remoteLoadListeners.remove(listener)
    }

    override fun isLoading(): Boolean = loading

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}
