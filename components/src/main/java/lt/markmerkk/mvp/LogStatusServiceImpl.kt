package lt.markmerkk.mvp

import lt.markmerkk.IDataStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.LogUtils
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

    private var lastShownLogId: Long = -1L
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

    fun handleResult(simpleLog: SimpleLog?) {
        if (simpleLog == null) {
            listener.hide()
            return
        }
        if (lastShownLogId == simpleLog._id) {
            // Same log is trying to be shown
            return
        }
        listener.show(
                toHeaderText(simpleLog),
                toBodyText(simpleLog)
        )
        lastShownLogId = simpleLog._id
    }

    fun toHeaderText(simpleLog: SimpleLog): String {
        val stringBuilder = StringBuilder()
        if (!simpleLog.task.isEmpty()) {
            stringBuilder.append(simpleLog.task)
            stringBuilder.append(" ")
        }
        stringBuilder.append("(")
        stringBuilder.append(LogUtils.formatShortDuration(simpleLog.duration))
        stringBuilder.append(")")
        return stringBuilder.toString()
    }

    fun toBodyText(simpleLog: SimpleLog): String {
        return simpleLog.comment
    }

    companion object {
        val logger = LoggerFactory.getLogger(LogStatusServiceImpl::class.java)!!
    }

}