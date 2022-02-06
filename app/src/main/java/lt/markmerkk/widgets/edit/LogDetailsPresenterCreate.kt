package lt.markmerkk.widgets.edit

import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.EventMainOpenTickets
import lt.markmerkk.mvp.LogEditInteractorImpl
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
import org.joda.time.DateTime

class LogDetailsPresenterCreate(
        private val logStorage: LogStorage,
        private val eventBus: WTEventBus,
        private val graphics: Graphics<SVGGlyph>,
        private val timeProvider: TimeProvider,
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
                    view?.showDateTime(start, end)
                }

                override fun onDurationChange(durationAsString: String) {
                    view?.showHint1(durationAsString)
                }

                override fun onGenericNotification(notification: String) {
                    view?.showHint2(notification)
                }

                override fun onEntitySaveComplete(start: DateTime, end: DateTime) {
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
        logEditService.entityInEdit = SimpleLogBuilder(now.millis)
            .setStart(now.millis)
            .setEnd(now.millis)
            .setTask("")
            .setComment("")
            .build()
        logEditService.serviceType = LogEditService.ServiceType.CREATE
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

    override fun save(start: DateTime, end: DateTime, task: String, comment: String) {
        logEditService.saveEntity(start, end, task, comment)
    }

    override fun changeDateTimeRaw(startDate: String, startTime: String, endDate: String, endTime: String) {
        logEditService.updateDateTimeRaw(startDate, startTime, endDate, endTime)
        logEditService.redraw()
    }

    override fun changeDateTime(start: DateTime, end: DateTime) {
        logEditService.updateDateTime(start, end)
        logEditService.redraw()
    }

    override fun openFindTickets() {
        eventBus.post(EventMainOpenTickets())
    }

    override fun changeTicketCode(ticket: String) { }

    override fun changeComment(comment: String) { }

}