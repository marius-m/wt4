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
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.mvp.*
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
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
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
    private lateinit var logEditService: LogEditService
    private lateinit var timeQuickModifier: TimeQuickModifier
    private lateinit var ticketInfoLoader: TicketInfoLoader

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        val dialogPadding = 100.0
        stageProperties.propertyWidth.addListener { _, _, newValue ->
            jfxDialogLayout.prefWidth = newValue.toDouble() - dialogPadding
        }
        stageProperties.propertyHeight.addListener { _, _, newValue ->
            jfxDialogLayout.prefHeight = newValue.toDouble() - dialogPadding
        }
        jfxDialogLayout.prefWidth = stageProperties.propertyWidth.get() - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.propertyHeight.get() - dialogPadding
        jfxButtonCancel.setOnAction { jfxDialog.close() }
        jfxButtonAccept.setOnAction {
            logEditService.saveEntity(
                    startDate = jfxDateFrom.value,
                    startTime = jfxTimeFrom.value,
                    endDate = jfxDateTo.value,
                    endTime = jfxTimeTo.value,
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
            val dialog = TicketsDialog()
            val jfxDialog = dialog.view as JFXDialog
            jfxDialog.show(jfxDialogLayout.parent as StackPane) // is this correct ?
            jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
        }
        val timeQuickModifierListener: TimeQuickModifier.Listener = object : TimeQuickModifier.Listener {
            override fun onTimeModified(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
                logEditService.updateDateTime(
                        startDateTime.toLocalDate(),
                        startDateTime.toLocalTime(),
                        endDateTime.toLocalDate(),
                        endDateTime.toLocalTime()
                )
                logEditService.redraw()
            }
        }
        timeQuickModifier = TimeQuickModifierImpl(
                timeQuickModifierListener
        )
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
                ioScheduler = Schedulers.io(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
        ticketInfoLoader.onAttach()
    }

    /**
     * Called directly from the view, whenever entity is ready for control.
     * If entity is provided, will provide update for the entity, otherwise it will create new one.
     */
    fun initFromView(entity: SimpleLog?) {
        logEditService = LogEditServiceImpl(
                LogEditInteractorImpl(logStorage),
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
                timeQuickModifier
        )
        uiBridgeDateTimeHandler = UIBridgeDateTimeHandler(
                jfxDateFrom = jfxDateFrom,
                jfxTimeFrom = jfxTimeFrom,
                jfxDateTo = jfxDateTo,
                jfxTimeTo = jfxTimeTo,
                timeQuickModifier = null,
                clockEditPresenter = null,
                logEditService = logEditService
        )
        if (entity != null) {
            logEditService.serviceType = LogEditService.ServiceType.UPDATE
            logEditService.entityInEdit = entity
            jfxHeaderLabel.text = "Update log"
            jfxButtonAccept.text = "UPDATE"
        } else {
            logEditService.serviceType = LogEditService.ServiceType.CREATE
            jfxHeaderLabel.text = "Create new log"
            jfxButtonAccept.text = "CREATE"
        }
        logEditService.redraw()
        uiBridgeDateTimeHandler.onAttach()
        eventBus.register(this)
    }

    @PreDestroy
    fun destroy() {
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

    //region Edit service callbacks

    override fun onDataChange(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            ticket: String,
            comment: String
    ) {
        uiBridgeDateTimeHandler.changeDate(startDateTime, endDateTime)
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

    //endregion

}