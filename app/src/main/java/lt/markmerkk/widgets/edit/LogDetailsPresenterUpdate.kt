package lt.markmerkk.widgets.edit

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.ViewProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl
import lt.markmerkk.utils.LogFormatters

class LogDetailsPresenterUpdate(
    private val viewProvider: ViewProvider<LogDetailsContract.View>,
    private val entityInEdit: Log,
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
        logEditService.initWithLog(entityInEdit)
        val logStart = LogFormatters.formatTime.print(entityInEdit.time.start)
        val logEnd = LogFormatters.formatTime.print(entityInEdit.time.end)
        viewProvider.invoke {
            this.initView(labelHeader = "Update log $logStart - $logEnd")
        }
    }

    override fun onDetach() {
    }

    override fun save() {
        logEditService.saveEntity()
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

}