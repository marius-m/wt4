package lt.markmerkk.widgets.edit

import lt.markmerkk.*
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl
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
        logEditService.bindLogByLocalId(localId = Const.NO_ID)
        logEditService.serviceType = LogEditService2.ServiceType.CREATE
        view.initView(
                labelHeader = "Active clock",
                labelButtonSave = "Save",
                glyphButtonSave = null,
                initDateTimeStart = timeProvider.roundDateTime(hourGlass.start.millis),
                initDateTimeEnd = timeProvider.roundDateTime(hourGlass.end.millis),
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