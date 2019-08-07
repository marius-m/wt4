package lt.markmerkk.widgets.edit

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.mvp.LogEditInteractorImpl
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
import org.joda.time.DateTime

class LogDetailsPresenterCreate(
        private val logStorage: LogStorage,
        private val hostServices: HostServicesInteractor,
        private val eventBus: EventBus,
        private val graphics: Graphics<SVGGlyph>,
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val resultDispatcher: ResultDispatcher,
        private val schedulerProvider: SchedulerProvider,
        private val timeProvider: TimeProvider
): LogDetailsContract.Presenter {

    private var view: LogDetailsContract.View? = null
    private val logEditService: LogEditService = LogEditServiceImpl(
            logEditInteractor = LogEditInteractorImpl(logStorage, timeProvider),
            timeProvider = timeProvider,
            listener = object : LogEditService.Listener {
                override fun onDataChange(
                        start: DateTime,
                        end: DateTime,
                        ticket: String,
                        comment: String
                ) {
                    view?.showDateTime(start, end)
                    view?.showTicket(ticket)
                    view?.showComment(comment)
                }

                override fun onDurationChange(durationAsString: String) {
                    view?.showHint1(durationAsString)
                }

                override fun onGenericNotification(notification: String) {
                    view?.showHint2(notification)
                }

                override fun onEntitySaveComplete() {
                    view?.close()
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
                labelHeader = "Log details (Create)",
                labelButtonSave = "Create",
                glyphButtonSave = graphics.from(Glyph.NEW, Color.BLACK, 12.0),
                initDateTimeStart = now,
                initDateTimeEnd = now
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

}