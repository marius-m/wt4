package lt.markmerkk.mvp

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.clone
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.entities.TimeGap.Companion.toTimeGap
import lt.markmerkk.utils.LogUtils

/**
 * Responsible for handling time change
 */
class LogEditService2Impl(
    private val timeProvider: TimeProvider,
    private val ticketStorage: TicketStorage,
    private val listener: LogEditService2.Listener,
    private val activeDisplayRepository: ActiveDisplayRepository,
) : LogEditService2 {

    override val timeGap: TimeGap
        get() = _timeGap
    private var initLog: Log = Log.createAsEmpty(timeProvider)
    private var _timeGap: TimeGap = TimeGap.asEmpty(timeProvider)
    private var codeRaw: String = ""
    private var comment: String = ""

    override fun initWithLog(log: Log) {
        this.initLog = log
        this._timeGap = log.time.toTimeGap()
        this.codeRaw = log.code.code
        this.comment = log.comment
        redraw()
    }

    override fun updateDateTime(timeGap: TimeGap) {
        this._timeGap = timeGap
        listener.showDateTimeChange(this._timeGap)
        listener.showDuration(LogUtils.formatShortDuration(timeGap.duration))
    }

    override fun updateCode(code: String) {
        this.codeRaw = code
    }

    override fun updateComment(comment: String) {
        this.comment = comment
    }

    override fun saveEntity() {
        val saveLog = initLog.clone(
            timeProvider = timeProvider,
            start = _timeGap.start,
            end = _timeGap.end,
            code = TicketCode.new(codeRaw),
            comment = comment
        )
        if (saveLog.hasId) {
            activeDisplayRepository.update(saveLog)
        } else {
            activeDisplayRepository.insertOrUpdate(saveLog)
        }
        ticketStorage.saveTicketAsUsedSync(timeProvider.preciseNow(), saveLog.code)
        listener.showSuccess(saveLog)
    }

    /**
     * Triggers according functions to show on screen
     */
    private fun redraw() {
        listener.showDateTimeChange(this._timeGap)
        listener.showDuration(LogUtils.formatShortDuration(_timeGap.duration))
        listener.showCode(TicketCode.new(codeRaw))
        listener.showComment(this.comment)
    }
}
