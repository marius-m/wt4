package lt.markmerkk.utils

import lt.markmerkk.*
import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.mvp.UserSettings
import net.rcarz.jiraclient.WorkLog
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by mariusmerkevicius on 1/5/16. Handles synchronization with jira from other components
 */
class SyncController2(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraInteractor: JiraInteractor,
        private val userSettings: UserSettings,
        private val logStorage: BasicLogStorage,
        private val remoteMergeToolsProvider: RemoteMergeToolsProvider,
        private val lastUpdateController: LastUpdateController,
        private val dayProvider: DayProvider,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    val remoteLoadListeners: MutableList<IRemoteLoadListener> = ArrayList()
    var subscription: Subscription? = null

    var isLoading = false
        set(value) {
            field = value
            remoteLoadListener.onLoadChange(value)
        }

    @PostConstruct
    fun init() { }

    @PreDestroy
    fun destroy() {
        subscription?.unsubscribe()
    }

    /**
     * Main method to start synchronization
     */
    fun sync() {
        if (isLoading) {
            logger.info("Sync is already loading")
            return
        }
        jiraClientProvider.reset()
        val uploadValidator = JiraUploadValidator()
        val downloadValidator = JiraDownloadWorklogValidator(
                userSettings.username,
                dayProvider.startDay(),
                dayProvider.endDay()
        )
        subscription = uploadObservable(uploadValidator)
                .flatMap { downloadObservable(downloadValidator) }
                .doOnSubscribe { isLoading = true }
                .doOnUnsubscribe { isLoading = false }
                .observeOn(uiScheduler)
                .subscribe({
                    logger.info("Sync complete!")
                }, {
                    logger.info("Sync terminate due to: ${it.message} / ${it.cause?.message?.substring(0, 40)}...")
                    remoteLoadListener.onError(it.message)
                }, {
                    logStorage.notifyDataChange()
                })
    }

    //region Observables

    fun uploadObservable(filter: JiraFilter<SimpleLog>): Observable<List<SimpleLog>> {
        return jiraInteractor.jiraLocalWorks()
                .flatMap { Observable.from(it) }
                .flatMap {
                    val pushMerger = remoteMergeToolsProvider.pushMerger(
                            localLog = it,
                            filter = filter
                    )
                    rx.util.async.Async.fromCallable(pushMerger, uiScheduler)
                }
                .toList()
    }

    fun downloadObservable(filter: JiraFilter<WorkLog>): Observable<List<JiraWork>> {
        return jiraInteractor.jiraRemoteWorks(dayProvider.startDay(), dayProvider.endDay())
                .flatMap { Observable.from(it) }
                .flatMap {
                    val pullMerger = remoteMergeToolsProvider.pullMerger(
                            it,
                            filter
                    )
                    rx.util.async.Async.fromCallable(pullMerger, uiScheduler)
                }
                .toList()
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

    //region Listeners

    internal val remoteLoadListener: IRemoteLoadListener = object : IRemoteLoadListener {
        override fun onLoadChange(loading: Boolean) {
            if (loading)
                lastUpdateController.error = null
            if (!loading)
                lastUpdateController.refresh()
            lastUpdateController.loading = loading
            for (remoteListeners in this@SyncController2.remoteLoadListeners)
                remoteListeners.onLoadChange(loading)
        }

        override fun onError(error: String) {
            lastUpdateController.error = error
            for (remoteListeners in this@SyncController2.remoteLoadListeners)
                remoteListeners.onError(error)
        }
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(JiraSearchSubscriberImpl::class.java)
    }

}
