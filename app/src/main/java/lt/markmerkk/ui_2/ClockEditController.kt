package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.*
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class ClockEditController : Initializable, ClockEditMVP.View {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonDismiss: JFXButton
    @FXML lateinit var jfxDateFrom: JFXDatePicker
    @FXML lateinit var jfxTimeFrom: JFXTimePicker
    @FXML lateinit var jfxDateTo: JFXDatePicker
    @FXML lateinit var jfxTimeTo: JFXTimePicker
    @FXML lateinit var jfxHint: Label
    @FXML lateinit var jfxSubtractFrom: JFXButton
    @FXML lateinit var jfxSubtractTo: JFXButton
    @FXML lateinit var jfxAppendFrom: JFXButton
    @FXML lateinit var jfxAppendTo: JFXButton
    @FXML lateinit var jfxTextFieldTicket: JFXTextField
    @FXML lateinit var jfxTextFieldComment: JFXTextArea
    @FXML lateinit var jfxButtonSave: JFXButton
    @FXML lateinit var jfxButtonSearch: JFXButton
    @FXML lateinit var jfxTextTicketDescription: Label
    @FXML lateinit var jfxTextFieldTicketLink: Hyperlink

    @Inject lateinit var hourglass: HourGlass
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var hostServices: HostServicesInteractor
    @Inject lateinit var activeLogPersistence: ActiveLogPersistence
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler

    private lateinit var clockEditPresenter: ClockEditMVP.Presenter
    private lateinit var logEditService: LogEditService
    private lateinit var ticketInfoLoader: TicketInfoLoader

    private val dialogPadding = 100.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        jfxButtonDismiss.setOnAction { jfxDialog.close() }
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding
        clockEditPresenter = ClockEditPresenterImpl(this, hourglass, timeProvider)
        logEditService = LogEditServiceImpl(
                LogEditInteractorImpl(storage, timeProvider),
                timeProvider,
                object : LogEditService.Listener {
                    override fun onDataChange(
                            start: DateTime,
                            end: DateTime,
                            ticket: String,
                            comment: String
                    ) {
                        uiBridgeDateTimeHandler.changeDate(start, end)
                    }

                    override fun onDurationChange(durationAsString: String) {
                        jfxHint.text = durationAsString
                    }

                    override fun onGenericNotification(notification: String) {}

                    override fun onEntitySaveComplete() {
                        eventBus.post(
                                EventSnackBarMessage(
                                        strings.getString("clock_event_save_complete")
                                )
                        )
                        hourglass.restart()
                        jfxDialog.close()
                    }

                    override fun onEntitySaveFail(error: Throwable) {
                        logger.error("Error saving log.", error)
                        jfxHint.text = error.message ?: "Error saving. Check logs for more info."
                    }

                    override fun onEnableInput() {}

                    override fun onDisableInput() {}

                    override fun onEnableSaving() {}

                    override fun onDisableSaving() {}

                }
        )
        logEditService.serviceType = LogEditService.ServiceType.CREATE
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
                clockEditPresenter = clockEditPresenter,
                logEditService = logEditService
        )
        jfxButtonSave.setOnAction {
            logEditService.saveEntity(
                    start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value),
                    end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value),
                    task = jfxTextFieldTicket.text,
                    comment = jfxTextFieldComment.text
            )
            activeLogPersistence.reset()
        }
        jfxButtonSearch.graphic = graphics.from(Glyph.SEARCH, Color.BLACK, 20.0, 20.0)
        jfxButtonSearch.setOnAction { eventBus.post(EventInflateDialog(DialogType.TICKET_SEARCH)) }
        jfxTextFieldTicket.textProperty().addListener { _, _, newValue ->
            ticketInfoLoader.changeInputCode(newValue)
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
        jfxTextFieldTicketLink.tooltip = Tooltip("Copy issue link to clipboard")
        jfxTextFieldTicketLink.graphic = graphics.from(Glyph.LINK, Color.BLACK, 16.0, 20.0)
        jfxTextFieldTicket.text = activeLogPersistence.ticketCode.code
        jfxTextFieldTicket.textProperty().addListener { _, _, newValue ->
            activeLogPersistence.changeTicketCode(newValue)
        }
        jfxTextFieldComment.text = activeLogPersistence.comment
        jfxTextFieldComment.textProperty().addListener { _, _, newValue ->
            activeLogPersistence.changeComment(newValue)
        }
        clockEditPresenter.onAttach()
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
        clockEditPresenter.onDetach()
    }

    //region Events

    @Subscribe
    fun eventSuggestTicket(eventSuggestTicket: EventSuggestTicket) {
        jfxTextFieldTicket.text = eventSuggestTicket.ticket.code.code
    }

    //endregion

    //region Listeners

    private val stageChangeListener: StageProperties.StageChangeListener = object : StageProperties.StageChangeListener {
        override fun onNewWidth(newWidth: Double) {
            jfxDialogLayout.prefWidth = newWidth - dialogPadding
        }

        override fun onNewHeight(newHeight: Double) {
            jfxDialogLayout.prefHeight = newHeight - dialogPadding
        }

        override fun onFocusChange(focus: Boolean) { }
    }

    override fun onDateChange(startDateTime: DateTime, endDateTime: DateTime) {
        uiBridgeDateTimeHandler.changeDate(startDateTime, endDateTime)
    }

    override fun onHintChange(hint: String) {
        jfxHint.text = hint
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(ClockEditController::class.java)!!
    }

}