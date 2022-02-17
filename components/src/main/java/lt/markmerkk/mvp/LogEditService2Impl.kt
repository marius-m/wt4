package lt.markmerkk.mvp

import lt.markmerkk.LogRepository
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.clone
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.utils.LogUtils

/**
 * Responsible for handling time change
 */
class LogEditService2Impl(
    private val timeProvider: TimeProvider,
    private val ticketStorage: TicketStorage,
    private val listener: LogEditService2.Listener,
    private val logRepository: LogRepository
) : LogEditService2 {

    override var serviceType: LogEditService2.ServiceType = LogEditService2.ServiceType.UPDATE
    private var log: Log = Log.createAsEmpty(timeProvider)

    override fun bindLogByLocalId(localId: Long) {
        log = logRepository.findByIdOrNull(localId) ?: Log.createAsEmpty(timeProvider)
    }

    override fun updateDateTime(timeGap: TimeGap) {
        log = updateTime(timeGap)
        listener.showDuration(LogUtils.formatShortDuration(log.time.duration))
        listener.lockEdit(isEnabled = true)
    }

    override fun saveEntity(
            timeGap: TimeGap,
            task: String,
            comment: String
    ) {
        val saveLog = log.clone(
            timeProvider = timeProvider,
            start = timeGap.start,
            end = timeGap.end,
            code = TicketCode.new(task),
            comment = comment
        )
        logRepository.insertOrUpdate(saveLog)
        log = saveLog
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
        changeEditLock(log)
    }

    /**
     * Generates a generic type of notification for the user
     */
    private fun changeEditLock(entity: Log) {
        if (entity.isRemote) {
            listener.lockEdit(isEnabled = false)
            return
        }
        if (entity.systemNote.isNotEmpty()) {
            listener.lockEdit(isEnabled = true)
            return
        }
        listener.lockEdit(isEnabled = true)
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