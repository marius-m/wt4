package lt.markmerkk.widgets.edit

import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.LogEditInteractorImpl
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime

class LogDetailsPresenterUpdateActiveClock(
        private val logStorage: LogStorage,
        private val eventBus: WTEventBus,
        private val graphics: Graphics<SVGGlyph>,
        private val timeProvider: TimeProvider,
        private val hourGlass: HourGlass,
        private val activeLogPersistence: ActiveLogPersistence,
        private val ticketStorage: TicketStorage
): LogDetailsContract.Presenter {

    private var view: LogDetailsContract.View? = null
    private val logEditService: LogEditService = LogEditServiceImpl(
            logEditInteractor = LogEditInteractorImpl(logStorage, timeProvider),
            timeProvider = timeProvider,
            ticketStorage = ticketStorage,
            listener = object : LogEditService.Listener {
                override fun onDataChange(
                        start: DateTime,
                        end: DateTime
                ) {
                    hourGlass.changeStart(start)
                    view?.showDateTime(start, end)
                }

                override fun onDurationChange(durationAsString: String) {
                    view?.showHint1(durationAsString)
                }

                override fun onGenericNotification(notification: String) {
                    view?.showHint2(notification)
                }

                override fun onEntitySaveComplete(start: DateTime, end: DateTime) {
                    hourGlass.startFrom(end)
                    activeLogPersistence.reset()
                    view?.closeDetails()
                }

                override fun onEntitySaveFail(error: Throwable) {
                    val errorMessage = error.message ?: "Error saving entity!"
                    view?.showHint1(errorMessage)
                }

                override fun onEnableInput() {
                    view?.enableInput()
                }

                override fun onDisableInput() {
                    view?.disableInput()
                }

                override fun onEnableSaving() {
                    view?.enableSaving()
                }

                override fun onDisableSaving() {
                    view?.disableSaving()
                }
            }
    )

    override fun onAttach(view: LogDetailsContract.View) {
        this.view = view
        val now = timeProvider.now()
        val startMillis = timeProvider.roundMillis(hourGlass.start)
        val endMillis = timeProvider.roundMillis(hourGlass.end)
        val entityInEdit = SimpleLogBuilder(now.millis)
                .setStart(startMillis)
                .setEnd(endMillis)
                .setTask(activeLogPersistence.ticketCode.code)
                .setComment(activeLogPersistence.comment)
                .build()
        logEditService.serviceType = LogEditService.ServiceType.CREATE
        logEditService.entityInEdit = entityInEdit
        view.initView(
                labelHeader = "Active clock",
                labelButtonSave = "Save",
                glyphButtonSave = null,
                initDateTimeStart = timeProvider.roundDateTime(entityInEdit.start),
                initDateTimeEnd = timeProvider.roundDateTime(entityInEdit.end),
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

    override fun save(start: DateTime, end: DateTime, task: String, comment: String) {
        logEditService.saveEntity(start, end, task, comment)
    }

    override fun changeDateTime(start: DateTime, end: DateTime) {
        logEditService.updateDateTime(start, end)
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