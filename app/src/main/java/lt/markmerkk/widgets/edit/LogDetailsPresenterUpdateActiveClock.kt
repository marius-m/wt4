package lt.markmerkk.widgets.edit

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Const
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.ViewProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.LogEditService2
import lt.markmerkk.mvp.LogEditService2Impl
import lt.markmerkk.utils.hourglass.HourGlass

class LogDetailsPresenterUpdateActiveClock(
    private val viewProvider: ViewProvider<LogDetailsContract.View>,
    private val eventBus: WTEventBus,
    private val timeProvider: TimeProvider,
    private val hourGlass: HourGlass,
    private val activeLogPersistence: ActiveLogPersistence,
    private val ticketStorage: TicketStorage,
    private val activeDisplayRepository: ActiveDisplayRepository,
    private val userSettings: UserSettings,
) : LogDetailsContract.Presenter {

    private val logEditService: LogEditService2 = LogEditService2Impl(
        timeProvider = timeProvider,
        ticketStorage = ticketStorage,
        activeDisplayRepository = activeDisplayRepository,
        listener = object : LogEditService2.Listener {
            override fun showDateTimeChange(timeGap: TimeGap) {
                hourGlass.changeStart(timeGap.start)
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
                if (userSettings.settingsAutoStartClock) {
                    hourGlass.startFrom(log.time.end)
                } else {
                    hourGlass.stop()
                }
                activeLogPersistence.reset()
                viewProvider.invoke { this.closeDetails() }
            }
        }
    )

    override fun onAttach() {
        val log = Log.new(
            timeProvider = timeProvider,
            start = hourGlass.start.millis,
            end = hourGlass.end.millis,
            code = activeLogPersistence.ticketCode.code,
            comment = activeLogPersistence.comment,
            systemNote = "",
            author = "",
            remoteData = null
        )
        logEditService.initWithLog(log)
        viewProvider.invoke { this.initView("Active clock") }
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
        activeLogPersistence.changeTicketCode(ticket)
        logEditService.updateCode(ticket)
    }

    override fun changeComment(comment: String) {
        activeLogPersistence.changeComment(comment)
        logEditService.updateComment(comment)
    }

}