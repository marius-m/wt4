package lt.markmerkk.widgets.edit

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Const
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.WTEventBus
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl
import lt.markmerkk.round
import lt.markmerkk.utils.hourglass.HourGlass

class LogDetailsPresenterUpdateActiveClock(
    private val eventBus: WTEventBus,
    private val timeProvider: TimeProvider,
    private val hourGlass: HourGlass,
    private val activeLogPersistence: ActiveLogPersistence,
    private val ticketStorage: TicketStorage,
    private val userSettings: UserSettings,
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
        logEditService.initByLocalId(localId = Const.NO_ID)
        view.initView(
            labelHeader = "Active clock",
            labelButtonSave = "Save",
            glyphButtonSave = null,
            initDateTimeStart = hourGlass.start.round(),
            initDateTimeEnd = hourGlass.end.round(),
            initTicket = activeLogPersistence.ticketCode.code,
            initComment = activeLogPersistence.comment,
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

    override fun changeTicketCode(ticket: String) {
        activeLogPersistence.changeTicketCode(ticket)
    }

    override fun changeComment(comment: String) {
        activeLogPersistence.changeComment(comment)
    }

}