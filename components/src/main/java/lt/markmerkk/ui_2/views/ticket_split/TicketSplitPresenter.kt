package lt.markmerkk.ui_2.views.ticket_split

import lt.markmerkk.*
import lt.markmerkk.entities.*
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.utils.LogSplitter

class TicketSplitPresenter(
        private val input: SimpleLog,
        private val timeProvider: TimeProvider,
        private val logStorage: LogStorage,
        private val logSplitter: LogSplitter,
        private val strings: Strings,
        private val ticketStorage: TicketStorage,
        private val schedulerProvider: SchedulerProvider
) : TicketSplitContract.Presenter {

    private var timeSplitPair: TimeSplitPair = logSplitter.split(
            timeGap = TimeGap.from(
                    timeProvider.roundDateTime(input.start),
                    timeProvider.roundDateTime(input.end)
            ),
            splitPercent = 50
    )
    private var view: TicketSplitContract.View? = null
    private val ticketInfoLoader = TicketInfoLoader(
            listener = object : TicketInfoLoader.Listener {
                override fun onTicketFound(ticket: Ticket) {
                    view?.showTicketLabel(ticketTitle = ticket.description)
                }

                override fun onNoTicket(searchTicket: String) {
                    view?.showTicketLabel(ticketTitle = "")
                }
            },
            ticketStorage = ticketStorage,
            waitScheduler = schedulerProvider.waitScheduler(),
            ioScheduler = schedulerProvider.io(),
            uiScheduler = schedulerProvider.ui()
    )


    override fun onAttach(view: TicketSplitContract.View) {
        this.view = view
        changeSplitBalance(balancePercent = 50)
        handleWorklogInit(simpleLog = input)
        ticketInfoLoader.onAttach()
        ticketInfoLoader.findTicket(input.task)
    }

    override fun onDetach() {
        ticketInfoLoader.onDetach()
        this.view = null
    }

    internal fun handleWorklogInit(simpleLog: SimpleLog) {
        view?.onWorklogInit(
                showTicket = simpleLog.task.isNotEmpty(),
                ticketCode = simpleLog.task,
                originalComment = simpleLog.comment,
                isSplitEnabled = !simpleLog.isRemote
        )
        view?.hideError()
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