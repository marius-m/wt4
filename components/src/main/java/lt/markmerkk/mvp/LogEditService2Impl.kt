package lt.markmerkk.mvp

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.clone
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.entities.toTimeGap
import lt.markmerkk.entities.toTimeGapRounded
import lt.markmerkk.utils.LogUtils

/**
 * Responsible for handling time change
 */
class LogEditService2Impl(
    private val timeProvider: TimeProvider,
    private val ticketStorage: TicketStorage,
    private val listener: LogEditService2.Listener,
    private val activeDisplayRepository: ActiveDisplayRepository,
    private val worklogStorage: WorklogStorage
) : LogEditService2 {

    private var log: Log = Log.createAsEmpty(timeProvider)

    override fun initByLocalId(localId: Long) {
        this.log = worklogStorage.findById(localId) ?: Log.createAsEmpty(timeProvider)
        listener.showDuration(LogUtils.formatShortDuration(log.time.duration))
    }

    override fun updateDateTime(timeGap: TimeGap) {
        this.log = updateTime(timeGap)
        listener.showDataTimeChange(log.toTimeGapRounded())
        listener.showDuration(LogUtils.formatShortDuration(log.time.duration))
    }

    override fun saveEntity(
            timeGap: TimeGap,
            task: String,
            comment: String
    ) {
        val saveLog = this.log.clone(
            timeProvider = timeProvider,
            start = timeGap.start,
            end = timeGap.end,
            code = TicketCode.new(task),
            comment = comment
        )
        activeDisplayRepository.insertOrUpdate(saveLog)
        this.log = saveLog
        ticketStorage.saveTicketAsUsedSync(timeProvider.preciseNow(), saveLog.code)
        listener.showSuccess()
    }

    /**
     * Triggers according functions to show on screen
     */
    override fun redraw() {
        val start = log.time.start
        val end = log.time.end
        listener.showDataTimeChange(TimeGap.from(start, end))
        listener.showDuration(LogUtils.formatShortDuration(log.time.duration))
    }

    private fun updateTime(
        timeGap: TimeGap
    ): Log {
        return log.clone(
            timeProvider = timeProvider,
            start = timeGap.start,
            end = timeGap.end
        )
    }

}