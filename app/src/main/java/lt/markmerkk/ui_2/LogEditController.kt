package lt.markmerkk.ui_2

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.mvp.*
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import org.joda.time.DateTime
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class LogEditController : Initializable, LogEditService.Listener {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonAccept: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton
    @FXML lateinit var jfxContentView: BorderPane
    @FXML lateinit var jfxHeaderLabel: Label

    @FXML lateinit var jfxDateFrom: JFXDatePicker
    @FXML lateinit var jfxTimeFrom: JFXTimePicker
    @FXML lateinit var jfxDateTo: JFXDatePicker
    @FXML lateinit var jfxTimeTo: JFXTimePicker
    @FXML lateinit var jfxTextFieldTicket: JFXTextField
    @FXML lateinit var jfxTextFieldTicketLink: Hyperlink
    @FXML lateinit var jfxTextFieldComment: JFXTextArea
    @FXML lateinit var jfxTextFieldHint: Label
    @FXML lateinit var jfxTextFieldHint2: Label
    @FXML lateinit var jfxSubtractFrom: JFXButton
    @FXML lateinit var jfxAppendFrom: JFXButton
    @FXML lateinit var jfxSubtractTo: JFXButton
    @FXML lateinit var jfxAppendTo: JFXButton
    @FXML lateinit var jfxButtonSearch: JFXButton
    @FXML lateinit var jfxTextTicketDescription: Label

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var hostServices: HostServicesInteractor
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
    private lateinit var logEditService: LogEditService
    private lateinit var ticketInfoLoader: TicketInfoLoader

    private val dialogPadding = 100.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding
        jfxButtonCancel.setOnAction { jfxDialog.close() }
        jfxButtonAccept.setOnAction {
            logEditService.saveEntity(
                    start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value),
                    end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value),
                    task = jfxTextFieldTicket.text,
                    comment = jfxTextFieldComment.text
            )
        }
        jfxTextFieldTicketLink.setOnAction {
            val issue = jfxTextFieldTicket.text.toString()
            eventBus.post(
                    EventSnackBarMessage(
                            String.format("Copied %s to clipboard", hostServices.generateLink(issue))
                    )
            )
            hostServices.copyLinkToClipboard(issue)
        }
        jfxTextFieldTicket.textProperty().addListener { _, _, newValue ->
            ticketInfoLoader.changeInputCode(newValue)
        }
        jfxTextFieldTicketLink.tooltip = Tooltip("Copy issue link to clipboard")
        jfxTextFieldTicketLink.graphic = graphics.from(Glyph.LINK, Color.BLACK, 16.0, 20.0)
        jfxButtonSearch.graphic = graphics.from(Glyph.SEARCH, Color.BLACK, 20.0, 20.0)
        jfxButtonSearch.setOnAction {
            eventBus.post(EventInflateDialog(DialogType.TICKET_SEARCH))
        }
        ticketInfoLoader = TicketInfoLoader(
                listener = object : TicketInfoLoader.Listener {
                    override fun onTicketFound(ticket: Ticket) {
                        jfxTextTicketDescription.text = ticket.description
                    }

                    override fun onNoTicket() {
                        jfxTextTicketDescription.text = ""
                    }
                },
                ticketsDatabaseRepo = ticketsDatabaseRepo,
                waitScheduler = schedulerProvider.waitScheduler(),
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        logEditService = LogEditServiceImpl(
                LogEditInteractorImpl(logStorage, timeProvider),
                timeProvider,
                this
        )
        uiBridgeTimeQuickEdit = UIBridgeTimeQuickEdit(
                jfxSubtractFrom,
                jfxSubtractTo,
                jfxAppendFrom,
                jfxAppendTo,
                jfxDateFrom,
                jfxTimeFrom,
                jfxDateTo,
                jfxTimeTo,
                logEditService,
                timeProvider
        )
        uiBridgeDateTimeHandler = UIBridgeDateTimeHandler(
                jfxDateFrom = jfxDateFrom,
                jfxTimeFrom = jfxTimeFrom,
                jfxDateTo = jfxDateTo,
                jfxTimeTo = jfxTimeTo,
                timeProvider = timeProvider,
                clockEditPresenter = null,
                logEditService = logEditService
        )
        val entity: SimpleLog? = resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)
        if (entity != null) {
            logEditService.serviceType = LogEditService.ServiceType.UPDATE
            logEditService.entityInEdit = entity
            jfxHeaderLabel.text = "Update log"
            jfxButtonAccept.text = "UPDATE"
            jfxButtonAccept.graphic = graphics.from(Glyph.UPDATE, Color.BLACK, 12.0)
        } else {
            logEditService.serviceType = LogEditService.ServiceType.CREATE
            jfxHeaderLabel.text = "Create new log"
            jfxButtonAccept.text = "CREATE"
            jfxButtonAccept.graphic = graphics.from(Glyph.NEW, Color.BLACK, 12.0)
        }
        logEditService.redraw()
        uiBridgeDateTimeHandler.onAttach()
        eventBus.register(this)
        ticketInfoLoader.onAttach()
        stageProperties.register(stageChangeListener)
    }

    @PreDestroy
    fun destroy() {
        stageProperties.unregister(stageChangeListener)
        ticketInfoLoader.onDetach()
        eventBus.unregister(this)
        uiBridgeDateTimeHandler.onDetach()
    }

    //region Events

    @Subscribe
    fun eventSuggestTicket(eventSuggestTicket: EventSuggestTicket) {
        jfxTextFieldTicket.text = eventSuggestTicket.ticket.code.code
    }

    //endregion

    //region Listeners

    override fun onDataChange(
            start: DateTime,
            end: DateTime,
            ticket: String,
            comment: String
    ) {
        uiBridgeDateTimeHandler.changeDate(start, end)
        jfxTextFieldTicket.text = ticket
        jfxTextFieldComment.text = comment
    }

    override fun onDurationChange(durationAsString: String) {
        jfxTextFieldHint.text = durationAsString
    }

    override fun onGenericNotification(notification: String) {
        jfxTextFieldHint2.text = notification
    }

    override fun onEntitySaveComplete() {
        jfxDialog.close()
    }

    override fun onEntitySaveFail(error: Throwable) {
        jfxTextFieldHint.text = error.message ?: "Error saving entity!"
    }

    // todo: Add disable clock/date selection when input is disabled
    override fun onEnableInput() {
        jfxTextFieldTicket.isEditable = true
        jfxTextFieldComment.isEditable = true
        uiBridgeDateTimeHandler.enable()
        uiBridgeTimeQuickEdit.enable()
    }

    // todo: Add disable clock/date selection when input is disabled
    override fun onDisableInput() {
        jfxTextFieldTicket.isEditable = false
        jfxTextFieldComment.isEditable = false
        uiBridgeDateTimeHandler.disable()
        uiBridgeTimeQuickEdit.disable()
    }

    override fun onEnableSaving() {
        jfxButtonAccept.isDisable = false
    }

    override fun onDisableSaving() {
        jfxButtonAccept.isDisable = true
    }

    private val stageChangeListener: StageProperties.StageChangeListener = object : StageProperties.StageChangeListener {
        override fun onNewWidth(newWidth: Double) {
            jfxDialogLayout.prefWidth = newWidth - dialogPadding
        }

        override fun onNewHeight(newHeight: Double) {
            jfxDialogLayout.prefHeight = newHeight - dialogPadding
        }

        override fun onFocusChange(focus: Boolean) { }
    }

    //endregion

    companion object {
        const val RESULT_DISPATCH_KEY_ENTITY = "AueWCx04wQ"
    }

}