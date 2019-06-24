package lt.markmerkk.ui_2.views.ticket_split

import lt.markmerkk.LogStorage
import lt.markmerkk.Strings
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.entities.TimeSplitPair
import lt.markmerkk.utils.LogSplitter

class TicketSplitPresenter(
        private val input: SimpleLog,
        private val timeProvider: TimeProvider,
        private val logStorage: LogStorage,
        private val logSplitter: LogSplitter,
        private val strings: Strings
) : TicketSplitContract.Presenter {

    private var timeSplitPair: TimeSplitPair = logSplitter.split(
            timeGap = TimeGap.from(
                    timeProvider.roundDateTime(input.start),
                    timeProvider.roundDateTime(input.end)
            ),
            splitPercent = 50
    )
    private var view: TicketSplitContract.View? = null

    override fun onAttach(view: TicketSplitContract.View) {
        this.view = view
        changeSplitBalance(balancePercent = 50)
        handleWorklogInit(input)
    }

    override fun onDetach() {
        this.view = null
    }

    internal fun handleWorklogInit(simpleLog: SimpleLog) {
        view?.onWorklogInit(
                showTicket = simpleLog.task.isNotEmpty(),
                ticketLabel = simpleLog.task,
                originalComment = simpleLog.comment,
                isSplitEnabled = !simpleLog.isRemote
        )
        if (simpleLog.isRemote) {
            view?.showError(strings.getString("ticket_split_error_remote_log"))
        } else {
            view?.hideError()
        }
    }

    override fun changeSplitBalance(balancePercent: Int) {
        timeSplitPair = logSplitter.split(
                timeGap = TimeGap.from(
                        timeProvider.roundDateTime(input.start),
                        timeProvider.roundDateTime(input.end)
                ),
                splitPercent = balancePercent
        )
        view?.onSplitTimeUpdate(
                start = timeSplitPair.first.start,
                end = timeSplitPair.second.end,
                splitGap = timeSplitPair.first.end
        )
    }

    override fun split(ticketName: String, originalComment: String, newComment: String) {
        val worklog1 = SimpleLogBuilder(timeProvider.now().millis)
                .setStart(timeProvider.roundMillis(timeSplitPair.first.start))
                .setEnd(timeProvider.roundMillis(timeSplitPair.first.end))
                .setTask(ticketName)
                .setComment(originalComment)
                .build()
        val worklog2 = SimpleLogBuilder(timeProvider.now().millis)
                .setStart(timeProvider.roundMillis(timeSplitPair.second.start))
                .setEnd(timeProvider.roundMillis(timeSplitPair.second.end))
                .setTask(ticketName)
                .setComment(newComment)
                .build()
        logStorage.delete(input)
        logStorage.insert(worklog1)
        logStorage.insert(worklog2)
    }

}