package lt.markmerkk.widgets.edit

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl
import lt.markmerkk.round
import lt.markmerkk.utils.LogFormatters

class LogDetailsPresenterUpdate(
    private val entityInEdit: Log,
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
        logEditService.initByLocalId(entityInEdit.id)
        val logStart = entityInEdit.time.start.round()
        val logStartFormatted = logStart.toString(LogFormatters.formatTime)
        val logEnd = entityInEdit.time.end.round()
        val logEndFormatted = logEnd.toString(LogFormatters.formatTime)
        view.initView(
                labelHeader = "Update log $logStartFormatted - $logEndFormatted",
                labelButtonSave = "Save",
                glyphButtonSave = null,
                initDateTimeStart = logStart,
                initDateTimeEnd = logEnd,
                initTicket = entityInEdit.code.code,
                initComment = entityInEdit.comment,
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