package lt.markmerkk.ui_2.views.calendar_edit

import lt.markmerkk.Const
import lt.markmerkk.LogRepository
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.clone
import lt.markmerkk.entities.Log.Companion.cloneAsNewLocal
import lt.markmerkk.entities.toTimeGapRounded
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.validators.TimeChangeValidator
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

class QuickEditPresenterMove(
    private val timeChangeValidator: TimeChangeValidator,
    private val timeProvider: TimeProvider,
    private val logChangeValidator: LogChangeValidator,
    private val selectEntryProvider: QuickEditContract.SelectEntryProvider,
    private val worklogStorage: WorklogStorage,
    private val logRepository: LogRepository
) : QuickEditContract.MovePresenter {

    private var view: QuickEditContract.MoveView? = null

    override fun onAttach(view: QuickEditContract.MoveView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun moveForward(minutes: Int): Long {
        val log: Log = worklogStorage.findById(selectEntryProvider.entryId()) ?: return Const.NO_ID
        val newTimeGap = timeChangeValidator.moveForward(
                log.toTimeGapRounded(timeProvider),
                minutes
        )
        val updateLogId = updateLog(log, newTimeGap.start, newTimeGap.end)
        selectEntryProvider.suggestNewEntry(updateLogId)
        return updateLogId
    }

    override fun moveBackward(minutes: Int): Long {
        val log = worklogStorage.findById(selectEntryProvider.entryId()) ?: return Const.NO_ID
        val newTimeGap = timeChangeValidator.moveBackward(
                log.toTimeGapRounded(timeProvider),
                minutes
        )
        val updateLogId = updateLog(log, newTimeGap.start, newTimeGap.end)
        selectEntryProvider.suggestNewEntry(updateLogId)
        return updateLogId
    }

    private fun updateLog(
            oldLog: Log,
            start: DateTime,
            end: DateTime
    ): Long {
        val newLog = oldLog.clone(
            timeProvider = timeProvider,
            start = start,
            end = end
        )
        if (logChangeValidator.canEditSimpleLog(selectEntryProvider.entryId())) {
            return logRepository.update(newLog)
        }
        return Const.NO_ID
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}
