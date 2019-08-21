package lt.markmerkk.interactors

import lt.markmerkk.*
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.worklogs.WorklogApi
import net.rcarz.jiraclient.WorkLog
import org.slf4j.LoggerFactory
import rx.*
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

    // todo replace this with event bus
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

    override fun syncAll() { }

    // todo will trigger multiple actions even if jiraclient fails. Handle this before release
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
                .flatMapCompletable { worklogApi.deleteUnknownLogs(Single.just(it), startDate, endDate) }
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
    }

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
