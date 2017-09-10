package lt.markmerkk.mvp

import lt.markmerkk.IDataStorage
import lt.markmerkk.entities.SimpleLog
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2017-09-09
 */
class LogStatusServiceImpl(
        private val listener: LogStatusService.Listener,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler,
        private val logStorage: IDataStorage<SimpleLog>
) : LogStatusService {

    override fun onAttach() {
    }

    override fun onDetach() {
        subscriptions.forEach { it.unsubscribe() }
    }

    private var lastShownLog: SimpleLog? = null
    private val subscriptions = mutableListOf<Subscription>()

    override fun showWithId(logId: Long?) {
        if (logId == null) {
            listener.hide()
            return
        }
        Observable.defer { Observable.just(logStorage.findByIdOrNull(logId)) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    handleResult(it)
                }, {
                    error ->
                    logger.error("[ERROR] Error looking for a log in database", error)
                }).let { subscriptions.add(it) }
    }

    private fun handleResult(simpleLog: SimpleLog?) {
        if (simpleLog == null) {
            listener.hide()
            return
        }
        if (lastShownLog != null && lastShownLog == simpleLog) {
            // Same log is trying to be shown
            return
        }
        listener.show(
                simpleLog.task,
                simpleLog.comment
        )
        lastShownLog = simpleLog
    }

    companion object {
        val logger = LoggerFactory.getLogger(LogStatusServiceImpl::class.java)!!
    }

}