package lt.markmerkk.interactors

import lt.markmerkk.*
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.worklogs.WorklogApi
import net.rcarz.jiraclient.WorkLog
import org.slf4j.LoggerFactory
import rx.Completable
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.util.async.Async
import java.util.concurrent.atomic.AtomicBoolean

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
        private val worklogApi: WorklogApi,
        private val worklogStorage: WorklogStorage,
        private val timeProvider: TimeProvider,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : SyncInteractor {

    val remoteLoadListeners = mutableListOf<IRemoteLoadListener>()
    var subscription: Subscription? = null

    val loading = AtomicBoolean(false)

    override fun onAttach() {}

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    fun changeLoadingState(isLoading: Boolean) {
        loading.set(isLoading)
        remoteLoadListeners.forEach { it.onLoadChange(loading.get()) }
    }

    override fun stop() {
        logger.info("Stopping sync")
        subscription?.unsubscribe()
    }

    override fun syncAll() {
//        if (loading.get()) {
//            logger.info("Sync in progress")
//            return
//        }
//        val syncStart = System.currentTimeMillis()
//        val uploadValidator = JiraFilterSimpleLog()
//        val downloadValidator = JiraFilterWorklog(
//                userSettings.username,
//                dayProvider.startDay(),
//                dayProvider.endDay()
//        )
//        subscription = uploadObservable(uploadValidator)
//                .flatMap { downloadObservable(downloadValidator) }
//                .subscribeOn(ioScheduler)
//                .observeOn(uiScheduler)
//                .doOnSubscribe { changeLoadingState(true) }
//                .doOnTerminate { changeLoadingState(false) }
//                .subscribe({
//                    val syncEnd = System.currentTimeMillis()
//                    logger.info("Sync all success in ${syncEnd - syncStart}ms!")
//                }, {
//                    logger.info("Sync all error: ${it.message} / ${it.cause?.message?.substring(0, 40)}...")
//                    logger.error("Sync all error data: ", it)
//                    val errorMsg = it.message
//                    remoteLoadListeners.forEach { it.onError(errorMsg) }
//                    logStorage.notifyDataChange()
//                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
//                }, {
//                    logStorage.notifyDataChange()
//                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
//                })
    }

    override fun syncLogs() {
        if (loading.get()) {
            logger.info("Sync in progress")
            return
        }
        val startDate = timeProvider.roundDateTime(dayProvider.startDay()).toLocalDate() // todo rm middle layer
        val endDate = timeProvider.roundDateTime(dayProvider.endDay()).toLocalDate() // todo rm middle layer
        val now = timeProvider.now()
        val syncStart = System.currentTimeMillis()
        subscription = Completable.fromAction { logger.info("Starting synchronization") }
                .andThen(worklogApi.deleteMarkedLogs(startDate, endDate))
                .andThen(worklogApi.uploadLogs(now, startDate, endDate))
                .andThen(worklogApi.fetchLogs(now, startDate, endDate))
                .andThen(worklogStorage.loadWorklogs(startDate, endDate))
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { changeLoadingState(true) }
                .doAfterTerminate {
                    changeLoadingState(false)
                    logStorage.notifyDataChange()
                    autoUpdateInteractor.notifyUpdateComplete(timeProvider.nowMillis())
                }
                .subscribe({
                    val syncEnd = System.currentTimeMillis()
                    logger.info("Log sync success in ${syncEnd - syncStart}ms!")
                }, {
                    logger.info("Log sync error: ${it.message}")
                    logger.error("Log sync error data: ", it)
                    logStorage.notifyDataChange()
                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
                })
//        subscription = worklogApi.fetchLogs(now, startDate, endDate)
//                .subscribeOn(ioScheduler)
//                .observeOn(uiScheduler)
//                .doOnSubscribe {
//                    changeLoadingState(true)
//                }
//                .doAfterTerminate {
//                    changeLoadingState(false)
//                    logStorage.notifyDataChange()
//                    autoUpdateInteractor.notifyUpdateComplete(timeProvider.nowMillis())
//                }
//                .subscribe({
//                    val syncEnd = System.currentTimeMillis()
//                    logger.info("Log sync success in ${syncEnd - syncStart}ms!")
//                }, {
//                    logger.info("Log sync error: ${it.message}")
//                    logger.error("Log sync error data: ", it)
//                    logStorage.notifyDataChange()
//                    autoUpdateInteractor.notifyUpdateComplete(System.currentTimeMillis())
//                })
    }

    //region Observables

    @Deprecated("")
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

    @Deprecated("")
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

    override fun isLoading(): Boolean = loading.get()

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}
