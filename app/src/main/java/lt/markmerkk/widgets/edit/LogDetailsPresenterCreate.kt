package lt.markmerkk.widgets.edit

import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl

class LogDetailsPresenterCreate(
    private val viewProvider: ViewProvider<LogDetailsContract.View>,
    private val eventBus: WTEventBus,
    private val timeProvider: TimeProvider,
    private val ticketStorage: TicketStorage,
    private val activeDisplayRepository: ActiveDisplayRepository,
) : LogDetailsContract.Presenter {

    private val logEditService: LogEditService2 = LogEditService2Impl(
        timeProvider = timeProvider,
        ticketStorage = ticketStorage,
        activeDisplayRepository = activeDisplayRepository,
        listener = object : LogEditService2.Listener {
            override fun showDateTimeChange(timeGap: TimeGap) {
                viewProvider.invoke { this.showDateTime(timeGap.start, timeGap.end) }
            }

            override fun showDuration(durationAsString: String) {
                viewProvider.invoke { this.showHint1(durationAsString) }
            }

            override fun showComment(comment: String) {
                viewProvider.invoke { this.showComment(comment) }
            }

            override fun showCode(ticketCode: TicketCode) {
                viewProvider.invoke { this.showTicketCode(ticketCode.code) }
            }

            override fun showSuccess(log: Log) {
                viewProvider.invoke { this.closeDetails() }
            }
        }
    )

    override fun onAttach() {
        logEditService.initWithLog(log = Log.createAsEmpty(timeProvider))
        viewProvider.invoke { this.initView("Create new log") }
    }

    override fun onDetach() {
    }

    override fun changeDateTime(timeGap: TimeGap) {
        logEditService.updateDateTime(timeGap)
    }

    override fun openFindTickets() {
        eventBus.post(EventMainOpenTickets())
    }

    override fun changeTicketCode(ticket: String) {
        logEditService.updateCode(ticket)
    }

    override fun changeComment(comment: String) {
        logEditService.updateComment(comment)
    }

    override fun save() {
        logEditService.saveEntity()
    }

}