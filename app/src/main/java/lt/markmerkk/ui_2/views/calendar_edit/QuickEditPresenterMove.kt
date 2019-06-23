package lt.markmerkk.ui_2.views.calendar_edit

import lt.markmerkk.Const
import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import org.joda.time.DateTime

class QuickEditPresenterMove(
        private val logStorage: LogStorage,
        private val timeChangeValidator: TimeChangeValidator,
        private val timeProvider: TimeProvider,
        private val logChangeValidator: LogChangeValidator
): QuickEditContract.MovePresenter {

    private var view: QuickEditContract.MoveView? = null
    private var selectLogId: Long = Const.NO_ID

    override fun onAttach(view: QuickEditContract.MoveView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun moveForward(minutes: Int) {
        val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
        val newTimeGap = timeChangeValidator.moveForward(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
    }

    override fun moveBackward(minutes: Int) {
        val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
        val newTimeGap = timeChangeValidator.moveBackward(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
    }

    override fun selectLogId(logId: Long) {
        this.selectLogId = logId
    }

    private fun updateLog(
            oldLog: SimpleLog,
            start: DateTime,
            end: DateTime
    ) {
        val newSimpleLog = SimpleLogBuilder(oldLog)
                .setStart(start.millis)
                .setEnd(end.millis)
                .build()
        if (logChangeValidator.canEditSimpleLog(selectLogId)) {
            logStorage.update(newSimpleLog)
        }
    }
}