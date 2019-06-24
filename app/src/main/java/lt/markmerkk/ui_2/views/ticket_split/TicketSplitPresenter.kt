package lt.markmerkk.ui_2.views.ticket_split

import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.utils.LogSplitter

class TicketSplitPresenter(
        private val inputSimpleLog: SimpleLog,
        private val timeProvider: TimeProvider,
        private val logStorage: LogStorage,
        private val logSplitter: LogSplitter
): TicketSplitContract.Presenter {

    private var view: TicketSplitContract.View? = null

    override fun onAttach(view: TicketSplitContract.View) {
        this.view = view
        changeSplitBalance(balancePercent = 50)
    }

    override fun onDetach() {
        this.view = null
    }

    override fun changeSplitBalance(balancePercent: Int) {
        val timeSplitPair = logSplitter.split(
                timeGap = TimeGap.from(
                        timeProvider.roundDateTime(inputSimpleLog.start),
                        timeProvider.roundDateTime(inputSimpleLog.end)
                ),
                splitPercent = balancePercent
        )
        view?.onSplitTimeUpdate(
                start = timeSplitPair.first.start,
                end = timeSplitPair.second.end,
                splitGap = timeSplitPair.first.end
        )
    }

    override fun split() {
        // not ready
    }

}