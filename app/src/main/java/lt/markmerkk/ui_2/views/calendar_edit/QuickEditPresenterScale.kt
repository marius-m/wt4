package lt.markmerkk.ui_2.views.calendar_edit

import lt.markmerkk.Const
import lt.markmerkk.LogStorage
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

class QuickEditPresenterScale(
        private val logStorage: LogStorage,
        private val timeChangeValidator: TimeChangeValidator,
        private val timeProvider: TimeProvider,
        private val logChangeValidator: LogChangeValidator,
        private val selectEntryProvider: QuickEditContract.SelectEntryProvider
): QuickEditContract.ScalePresenter {

    private var view: QuickEditContract.ScaleView? = null

    override fun onAttach(view: QuickEditContract.ScaleView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun shrinkFromStart(minutes: Int): Long {
        val simpleLog = logStorage.findByIdOrNull(selectEntryProvider.entryId()) ?: return Const.NO_ID
        val newTimeGap = timeChangeValidator.shrinkFromStart(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        val updateLogId = updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        selectEntryProvider.suggestNewEntry(updateLogId)
        return updateLogId
    }

    override fun expandToStart(minutes: Int): Long {
        val simpleLog = logStorage.findByIdOrNull(selectEntryProvider.entryId()) ?: return Const.NO_ID
        val newTimeGap = timeChangeValidator.expandToStart(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        val updateLogId = updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        selectEntryProvider.suggestNewEntry(updateLogId)
        return updateLogId
    }

    override fun shrinkFromEnd(minutes: Int): Long {
        val simpleLog = logStorage.findByIdOrNull(selectEntryProvider.entryId()) ?: return Const.NO_ID
        val newTimeGap = timeChangeValidator.shrinkFromEnd(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        val updateLogId = updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        selectEntryProvider.suggestNewEntry(updateLogId)
        return updateLogId
    }

    override fun expandToEnd(minutes: Int): Long {
        val simpleLog = logStorage.findByIdOrNull(selectEntryProvider.entryId()) ?: return Const.NO_ID
        val newTimeGap = timeChangeValidator.expandToEnd(
                TimeGap.from(
                        timeProvider.roundDateTime(simpleLog.start),
                        timeProvider.roundDateTime(simpleLog.end)
                ),
                minutes
        )
        val updateLogId = updateLog(simpleLog, newTimeGap.start, newTimeGap.end)
        selectEntryProvider.suggestNewEntry(updateLogId)
        return updateLogId
    }

    private fun updateLog(
            oldLog: SimpleLog,
            start: DateTime,
            end: DateTime
    ): Long {
        val newSimpleLog = SimpleLogBuilder(oldLog)
                .setStart(start.millis)
                .setEnd(end.millis)
                .build()
        if (logChangeValidator.canEditSimpleLog(selectEntryProvider.entryId())) {
            return logStorage.update(newSimpleLog).toLong()
        }
        return Const.NO_ID
    }

    companion object {
        private val logger = LoggerFactory.getLogger(QuickEditPresenterScale::class.java)!!
    }

}