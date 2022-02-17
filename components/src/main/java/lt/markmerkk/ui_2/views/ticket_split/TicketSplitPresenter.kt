package lt.markmerkk.ui_2.views.ticket_split

import lt.markmerkk.*
import lt.markmerkk.entities.*
import lt.markmerkk.entities.Log.Companion.cloneAsNewLocal
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.utils.LogSplitter

class TicketSplitPresenter(
    private val input: Log,
    private val timeProvider: TimeProvider,
    private val logSplitter: LogSplitter,
    private val strings: Strings,
    private val ticketStorage: TicketStorage,
    private val schedulerProvider: SchedulerProvider,
    private val logRepository: LogRepository
) : TicketSplitContract.Presenter {

    private var timeSplitPair: TimeSplitPair = logSplitter.split(
            timeGap = TimeGap.from(
                    timeProvider.roundDateTime(input.time.start.millis),
                    timeProvider.roundDateTime(input.time.end.millis)
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
        ticketInfoLoader.findTicket(input.code.code)
    }

    override fun onDetach() {
        ticketInfoLoader.onDetach()
        this.view = null
    }

    internal fun handleWorklogInit(simpleLog: Log) {
        view?.onWorklogInit(
                showTicket = !simpleLog.code.isEmpty(),
                ticketCode = simpleLog.code.code,
                originalComment = simpleLog.comment,
                isSplitEnabled = !simpleLog.isRemote
        )
        view?.hideError()
    }

    override fun changeSplitBalance(balancePercent: Int) {
        timeSplitPair = logSplitter.split(
                timeGap = TimeGap.from(
                        timeProvider.roundDateTime(input.time.start.millis),
                        timeProvider.roundDateTime(input.time.end.millis)
                ),
                splitPercent = balancePercent
        )
        view?.onSplitTimeUpdate(
                start = timeSplitPair.first.start,
                end = timeSplitPair.second.end,
                splitGap = timeSplitPair.first.end,
                durationStart = timeSplitPair.first.duration,
                durationEnd = timeSplitPair.second.duration
        )
    }

    override fun split(ticketName: String, originalComment: String, newComment: String) {
        val worklog1 = input.cloneAsNewLocal(
            timeProvider = timeProvider,
            start = timeSplitPair.first.start,
            end = timeSplitPair.first.end,
            code = TicketCode.new(ticketName),
            comment = originalComment
        )
        val worklog2 = input.cloneAsNewLocal(
            timeProvider = timeProvider,
            start = timeSplitPair.second.start,
            end = timeSplitPair.second.end,
            code = TicketCode.new(ticketName),
            comment = newComment
        )
        logRepository.delete(input)
        logRepository.insertOrUpdate(worklog1)
        logRepository.insertOrUpdate(worklog2)
    }

}