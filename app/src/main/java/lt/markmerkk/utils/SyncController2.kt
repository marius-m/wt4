package lt.markmerkk.utils

import lt.markmerkk.*
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteLogMerger
import lt.markmerkk.merger.RemoteMergeExecutorImpl
import lt.markmerkk.storage2.BasicLogStorage
import lt.markmerkk.storage2.IDataStorage
import lt.markmerkk.storage2.SimpleLog
import lt.markmerkk.storage2.database.interfaces.IExecutor
import lt.markmerkk.ui.utils.DisplayType
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by mariusmerkevicius on 1/5/16. Handles synchronization with jira from other components
 */
class SyncController2(
        val settings: UserSettings,
        val dbExecutor: IExecutor,
        val logStorage: BasicLogStorage,
        val lastUpdateController: LastUpdateController,
        val ioScheduler: Scheduler,
        val uiScheduler: Scheduler
) {

    val remoteLoadListeners: MutableList<IRemoteLoadListener> = ArrayList()
    var subscription: Subscription? = null

    var isLoading = false
        private set

    @PostConstruct
    fun init() { }

    @PreDestroy
    fun destroy() {
        subscription?.unsubscribe()
    }


    fun sync() {
        sync(
                startDay(),
                endDay(),
                Schedulers.computation(),
                JavaFxScheduler.getInstance()
        )
    }

    /**
     * Main method to start synchronization
     */
    fun sync(
            start: DateTime,
            end: DateTime,
            uiScheduler: Scheduler,
            ioScheduler: Scheduler
    ) {
        if (isLoading) {
            logger.info("Sync is already loading")
            return
        }
        val jiraClientProvider = JiraClientProviderImpl(
                host = settings.host,
                username = settings.username,
                password = settings.password
        )
        val jiraInteractor = JiraInteractorImpl(
                jiraClientProvider = jiraClientProvider,
                jiraSearchSubsciber = JiraSearchSubscriberImpl(jiraClientProvider),
                jiraWorklogSubscriber = JiraWorklogSubscriberImpl(jiraClientProvider)
        )
        val remoteMergeExecutor = RemoteMergeExecutorImpl(dbExecutor)
        subscription = jiraInteractor.jiraWorks(start, end)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe {
                    isLoading = true
                    remoteLoadListener.onLoadChange(true)
                }
                .doOnUnsubscribe {
                    isLoading = false
                    remoteLoadListener.onLoadChange(false)
                }
                .flatMap { Observable.from(it) }
                .flatMap {
                    rx.util.async.Async.fromRunnable(
                            RemoteLogMerger(
                                    remoteMergeExecutor,
                                    JiraLogFilter(jiraClientProvider.username!!, start, end),
                                    it),
                            it)
                }
                .subscribe({
                    logger.info("Success!")
                }, {
                    logger.info("Error synchronizing: ${it.message}")
                    remoteLoadListener.onError(it.message)
                })

//        if (jiraClient == null)
//            return

//        val filterer = JiraLogFilterer(
//                settings!!.getUsername(),
//                startTime,
//                endTime)

//        val remoteLogFetchMerger = RemoteLogFetchMerger(dbExecutor)
//        val remoteLogPushMerger = RemoteLogPushMerger(dbExecutor, jiraClient)

//        val downloadObservable = JiraObservables
//                .remoteWorklogs(jiraClient, filterer, startTime, endTime)
//                .map<String>({ pair ->
//                    for (workLog in pair.getValue())
//                        remoteLogFetchMerger.merge(pair.getKey().getKey(), workLog)
//                    null
//                })

//        val uploadObservable = Observable.from(storage!!.data)
//                .map<String>({ simpleLog ->
//                    remoteLogPushMerger.merge(simpleLog)
//                    null
//                })

//        remoteLoadListener.onLoadChange(true)
//        subscription = downloadObservable
//                .startWith(uploadObservable)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(JavaFxScheduler.getInstance())
//                .subscribe({ output ->
//                    logger.info(output);
//                },
//                        { error ->
//                            logger.info("Sync error!  " + error)
//                            remoteLoadListener.onLoadChange(false)
//                            remoteLoadListener.onError(error.message)
//                        }) {
//                    logger.info("Sync complete! ")
//                    remoteLoadListener.onLoadChange(false)
//                    storage!!.notifyDataChange()
//                }
    }

    //region Convenience

    fun startDay(): DateTime {
        when (logStorage.displayType) {
            DisplayType.WEEK -> return logStorage.targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
            else -> return logStorage.targetDate
        }
    }

    fun endDay(): DateTime {
        when (logStorage.displayType) {
            DisplayType.WEEK -> return logStorage.targetDate.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withTimeAtStartOfDay()
            else -> return logStorage.targetDate.plusDays(1)
        }
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
            this@SyncController2.isLoading = loading
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
