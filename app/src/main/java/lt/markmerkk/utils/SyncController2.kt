package lt.markmerkk.utils

import lt.markmerkk.DBProdExecutor
import lt.markmerkk.JiraConnector
import lt.markmerkk.JiraSearchJQL
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.storage2.BasicLogStorage
import lt.markmerkk.storage2.IDataStorage
import lt.markmerkk.storage2.SimpleLog
import lt.markmerkk.storage2.database.interfaces.IExecutor
import lt.markmerkk.ui.utils.DisplayType
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by mariusmerkevicius on 1/5/16. Handles synchronization with jira from other components
 */
class SyncController2(
        val settings: UserSettings,
        val dbExecutor: IExecutor,
        val logStorage: IDataStorage<SimpleLog>,
        val lastUpdateController: LastUpdateController
) {

    val remoteLoadListeners: MutableList<IRemoteLoadListener> = ArrayList()
    var subscription: Subscription? = null

    var jiraClient: JiraClient? = null
        private set

    var isLoading = false
        private set

    @PostConstruct
    fun init() { }

    @PreDestroy
    fun destroy() {
        subscription?.unsubscribe()
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
        clientObservable()
                .doOnSubscribe { remoteLoadListener.onLoadChange(true) }
                .doOnUnsubscribe { remoteLoadListener.onLoadChange(false) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .map { this.jiraClient = it }
                .subscribe({
                    logger.info("Success!")
                }, {
                    logger.info(it.message)
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

//    fun startDay(): DateTime {
//        when (storage.displayType) {
//            DisplayType.WEEK -> return storage.targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
//            else -> return storage.targetDate
//        }
//    }

//    fun endDay(): DateTime {
//        when (storage.displayType) {
//            DisplayType.WEEK -> return storage.targetDate.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withTimeAtStartOfDay()
//            else -> return storage.targetDate.plusDays(1)
//        }
//    }

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

    //region Observables

    /**
     * Returns an observable for jira client initialization
     */
    fun clientObservable(): Observable<JiraClient> {
        if (jiraClient == null) {
            return Observable.create(JiraConnector(
                    settings.host,
                    settings.username,
                    settings.password)
            )
        }
        return Observable.just(jiraClient)
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(JiraSearchJQL::class.java)
    }

}
