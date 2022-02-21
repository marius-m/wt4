package lt.markmerkk.widgets.edit

import lt.markmerkk.*
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl

class LogDetailsPresenterCreate(
    private val eventBus: WTEventBus,
    private val timeProvider: TimeProvider,
    private val ticketStorage: TicketStorage,
    private val activeDisplayRepository: ActiveDisplayRepository,
    private val worklogStorage: WorklogStorage
) : LogDetailsContract.Presenter {

    private var view: LogDetailsContract.View? = null
    private val logEditService: LogEditService2 = LogEditService2Impl(
        timeProvider = timeProvider,
        ticketStorage = ticketStorage,
        activeDisplayRepository = activeDisplayRepository,
        worklogStorage = worklogStorage,
        listener = object : LogEditService2.Listener {
            override fun showDataTimeChange(timeGap: TimeGap) {
                view?.showDateTime(timeGap.start, timeGap.end)
            }

            override fun showDuration(durationAsString: String) {
                view?.showHint1(durationAsString)
            }

            override fun showSuccess() {
                view?.closeDetails()
            }
        }
    )

    override fun onAttach(view: LogDetailsContract.View) {
        this.view = view
        val now = timeProvider.now()
        view.initView(
                labelHeader = "Create new log",
                labelButtonSave = "Save",
                glyphButtonSave = null,
                initDateTimeStart = now,
                initDateTimeEnd = now,
                initTicket = "",
                initComment = "",
                enableFindTickets = true,
                enableDateTimeChange = true
        )
        logEditService.redraw()
    }

    override fun onDetach() {
        this.view = null
    }

    override fun save(timeGap: TimeGap, task: String, comment: String) {
        logEditService.saveEntity(timeGap, task, comment)
    }

    override fun changeDateTime(timeGap: TimeGap) {
        logEditService.updateDateTime(timeGap)
        logEditService.redraw()
    }

    override fun openFindTickets() {
        eventBus.post(EventMainOpenTickets())
    }

    override fun changeTicketCode(ticket: String) { }

    override fun changeComment(comment: String) { }

}