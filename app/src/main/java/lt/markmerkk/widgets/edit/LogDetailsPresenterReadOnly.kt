package lt.markmerkk.widgets.edit

import com.google.common.eventbus.EventBus
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl

class LogDetailsPresenterReadOnly(
    private val entityInEdit: SimpleLog,
    private val eventBus: EventBus,
    private val timeProvider: TimeProvider,
    private val ticketStorage: TicketStorage,
    private val logRepository: LogRepository
) : LogDetailsContract.Presenter {

    private var view: LogDetailsContract.View? = null
    private val logEditService: LogEditService2 = LogEditService2Impl(
        timeProvider = timeProvider,
        ticketStorage = ticketStorage,
        logRepository = logRepository,
        listener = object : LogEditService2.Listener {
            override fun showDataTimeChange(timeGap: TimeGap) {
                view?.showDateTime(timeGap.start, timeGap.end)
            }

            override fun showDuration(durationAsString: String) {
                view?.showHint1(durationAsString)
            }

            override fun lockEdit(isEnabled: Boolean) {
                if (isEnabled) {
                    view?.enableInput()
                    view?.enableSaving()
                } else {
                    view?.disableInput()
                    view?.disableSaving()
                }
            }

            override fun showSuccess() {
                view?.closeDetails()
            }
        }
    )

    override fun onAttach(view: LogDetailsContract.View) {
        this.view = view
        logEditService.bindLogByLocalId(entityInEdit.id)
        logEditService.serviceType = LogEditService2.ServiceType.UPDATE
        view.initView(
                labelHeader = "Log details (Read-only)",
                labelButtonSave = "Save",
                glyphButtonSave = null,
                initDateTimeStart = timeProvider.roundDateTime(entityInEdit.start),
                initDateTimeEnd = timeProvider.roundDateTime(entityInEdit.end),
                initTicket = entityInEdit.task,
                initComment = entityInEdit.comment,
                enableFindTickets = false,
                enableDateTimeChange = false
        )
        view.disableInput()
        view.disableSaving()
        logEditService.redraw()
    }

    override fun onDetach() {
        this.view = null
    }

    override fun save(timeGap: TimeGap, task: String, comment: String) {
        eventBus.post(EventSnackBarMessage("Ticket in 'Read-only' mode, cannot be updated!"))
    }

    override fun changeDateTime(timeGap: TimeGap) {
        logEditService.updateDateTime(timeGap)
        logEditService.redraw()
    }

    override fun openFindTickets() {
        eventBus.post(EventSnackBarMessage("Ticket in 'Read-only' mode, cannot be updated!"))
    }

    override fun changeTicketCode(ticket: String) { }

    override fun changeComment(comment: String) { }

}