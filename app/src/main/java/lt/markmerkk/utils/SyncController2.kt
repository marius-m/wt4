package lt.markmerkk.utils

import lt.markmerkk.JiraInteractor
import lt.markmerkk.JiraLogFilter
import lt.markmerkk.JiraSearchSubscriberImpl
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteMergeExecutor
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
        private val jiraInteractor: JiraInteractor,
        private val userSettings: UserSettings,
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


    fun syncWithDefaultValues() {
        sync(
                Schedulers.computation(),
                JavaFxScheduler.getInstance()
        )
    }

    /**
     * Main method to start synchronization
     */
    fun sync(
            uiScheduler: Scheduler,
            ioScheduler: Scheduler
    ) {
        if (isLoading) {
            logger.info("Sync is already loading")
            return
        }
        subscription = jiraInteractor.jiraWorks(dayProvider.startDay(), dayProvider.endDay())
                .subscribeOn(ioScheduler)
                .doOnSubscribe { isLoading = true }
                .doOnUnsubscribe { isLoading = false }
                .flatMap {
                    Observable.from(it)
                }
                .flatMap {
                    val fetchMerger = remoteMergeToolsProvider.fetchMerger(
                                    it,
                                    JiraLogFilter(
                                            user = userSettings.username,
                                            start = dayProvider.startDay(),
                                            end = dayProvider.endDay()
                                    )
                            )
                    rx.util.async.Async.fromCallable(fetchMerger)
                }
                .observeOn(uiScheduler)
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
