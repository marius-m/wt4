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

class QuickEditPresenterScale(
        private val logStorage: LogStorage,
        private val timeChangeValidator: TimeChangeValidator,
        private val timeProvider: TimeProvider,
        private val logChangeValidator: LogChangeValidator
): QuickEditContract.ScalePresenter {

    private var view: QuickEditContract.ScaleView? = null
    private var selectLogId: Long = Const.NO_ID

    override fun onAttach(view: QuickEditContract.ScaleView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun shrinkFromStart(minutes: Int) {
        val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
        val newTimeGap = timeChangeValidator.shrinkFromStart(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
    }

    override fun expandToStart(minutes: Int) {
        val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
        val newTimeGap = timeChangeValidator.expandToStart(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
    }

    override fun shrinkFromEnd(minutes: Int) {
        val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
        val newTimeGap = timeChangeValidator.shrinkFromEnd(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
    }

    override fun expandToEnd(minutes: Int) {
        val simpleLog = logStorage.findByIdOrNull(selectLogId) ?: return
        val newTimeGap = timeChangeValidator.expandToEnd(
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