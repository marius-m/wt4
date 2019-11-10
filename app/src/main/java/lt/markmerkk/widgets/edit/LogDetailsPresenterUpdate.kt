package lt.markmerkk.widgets.edit

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.mvp.LogEditInteractorImpl
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
import org.joda.time.DateTime

class LogDetailsPresenterUpdate(
        private val entityInEdit: SimpleLog,
        private val logStorage: LogStorage,
        private val eventBus: WTEventBus,
        private val graphics: Graphics<SVGGlyph>,
        private val timeProvider: TimeProvider
): LogDetailsContract.Presenter {

    private var view: LogDetailsContract.View? = null
    private val logEditService: LogEditService = LogEditServiceImpl(
            logEditInteractor = LogEditInteractorImpl(logStorage, timeProvider),
            timeProvider = timeProvider,
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

                override fun onEntitySaveComplete() {
                    view?.close()
                }

                override fun onEntitySaveFail(error: Throwable) {
                    val errorMessage = error.message ?: "Error saving entity!"
                    view?.showHint1(errorMessage)
                }

                override fun onEnableInput() { }

                override fun onDisableInput() { }

                override fun onEnableSaving() { }

                override fun onDisableSaving() { }
            }
    )

    override fun onAttach(view: LogDetailsContract.View) {
        this.view = view
        logEditService.entityInEdit = entityInEdit
        logEditService.serviceType = LogEditService.ServiceType.UPDATE
        view.initView(
                labelHeader = "Log details (Update)",
                labelButtonSave = "Update",
                glyphButtonSave = graphics.from(Glyph.UPDATE, Color.BLACK, 12.0),
                initDateTimeStart = timeProvider.roundDateTime(entityInEdit.start),
                initDateTimeEnd = timeProvider.roundDateTime(entityInEdit.end),
                initTicket = entityInEdit.task,
                initComment = entityInEdit.comment,
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
        eventBus.post(EventInflateDialog(DialogType.TICKET_SEARCH))
    }

    override fun changeTicketCode(ticket: String) { }

    override fun changeComment(comment: String) { }

}