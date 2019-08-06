package lt.markmerkk.widgets.edit

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.mvp.LogEditInteractorImpl
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.LogEditServiceImpl
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.ui_2.views.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class LogDetailsWidget: View(), LogDetailsContract.View {

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var viewLabelHeader: Label
    private lateinit var viewDatePickerFrom: JFXDatePicker
    private lateinit var viewTimePickerFrom: JFXTimePicker
    private lateinit var viewButtonSubtractFrom: JFXButton
    private lateinit var viewButtonAppendFrom: JFXButton
    private lateinit var viewDatePickerTo: JFXDatePicker
    private lateinit var viewTimePickerTo: JFXTimePicker
    private lateinit var viewButtonSubtractTo: JFXButton
    private lateinit var viewButtonAppendTo: JFXButton
    private lateinit var viewTextFieldTicket: JFXTextField
    private lateinit var viewTextTicketDesc: Label
    private lateinit var viewButtonTicketLink: JFXButton
    private lateinit var viewButtonSearch: JFXButton
    private lateinit var viewTextComment: JFXTextArea
    private lateinit var viewButtonSave: JFXButton
    private lateinit var viewButtonDismiss: JFXButton
    private lateinit var viewLabelHint: Label
    private lateinit var viewLabelHint2: Label

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
    private var presenter: LogDetailsContract.Presenter
    private var logEditService: LogEditService
    private val ticketInfoLoader: TicketInfoLoader

    init {
        Main.component().inject(this)
        presenter = LogDetailsPresenter(
                logStorage,
                hostServicesInteractor,
                eventBus,
                graphics,
                ticketsDatabaseRepo,
                resultDispatcher,
                schedulerProvider,
                timeProvider
        )
        ticketInfoLoader = TicketInfoLoader(
                listener = object : TicketInfoLoader.Listener {
                    override fun onTicketFound(ticket: Ticket) {
                        viewTextTicketDesc.text = ticket.description
                    }

                    override fun onNoTicket() {
                        viewTextTicketDesc.text = ""
                    }
                },
                ticketsDatabaseRepo = ticketsDatabaseRepo,
                waitScheduler = schedulerProvider.waitScheduler(),
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        logEditService = LogEditServiceImpl(
                logEditInteractor = LogEditInteractorImpl(logStorage, timeProvider),
                timeProvider = timeProvider,
                listener = object : LogEditService.Listener {
                    override fun onDataChange(start: DateTime, end: DateTime, ticket: String, comment: String) {
                        uiBridgeDateTimeHandler.changeDate(start, end)
                        viewTextFieldTicket.text = ticket
                        viewTextComment.text = comment
                    }

                    override fun onDurationChange(durationAsString: String) {
                        viewLabelHint.text = durationAsString
                    }

                    override fun onGenericNotification(notification: String) {
                        viewLabelHint2.text = notification
                    }

                    override fun onEntitySaveComplete() {
                        close()
                    }

                    override fun onEntitySaveFail(error: Throwable) {
                        viewLabelHint.text = error.message ?: "Error saving entity!"
                    }

                    override fun onEnableInput() {
                        viewTextFieldTicket.isEditable = true
                        viewTextComment.isEditable = true
                        uiBridgeDateTimeHandler.enable()
                        uiBridgeTimeQuickEdit.enable()
                    }

                    override fun onDisableInput() {
                        viewTextFieldTicket.isEditable = false
                        viewTextComment.isEditable = false
                        uiBridgeDateTimeHandler.disable()
                        uiBridgeTimeQuickEdit.disable()
                    }

                    override fun onEnableSaving() {
                        viewButtonSave.isDisable = false
                    }

                    override fun onDisableSaving() {
                        viewButtonSave.isDisable = true
                    }
                }
        )
    }

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            viewLabelHeader = label("Log details") {
                addClass(Styles.dialogHeader)
            }
        }
        center {
            vbox {
                label("From") {
                    addClass(Styles.labelMini)
                    style {
                        padding = box(
                                top = 10.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                }
                hbox(spacing = 4) {
                    viewDatePickerFrom = jfxDatePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewTimePickerFrom = jfxTimePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewButtonSubtractFrom = jfxButton("-") {
                        isFocusTraversable = false
                    }
                    viewButtonAppendFrom = jfxButton("+") {
                        isFocusTraversable = false
                    }
                }
                label("To") {
                    addClass(Styles.labelMini)
                    style {
                        padding = box(
                                top = 10.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                }
                hbox(spacing = 4) {
                    viewDatePickerTo = jfxDatePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewTimePickerTo = jfxTimePicker {
                        isFocusTraversable = false
                        isOverLay = true
                        defaultColor = Styles.cActiveRed
                    }
                    viewButtonSubtractTo = jfxButton("-") {
                        isFocusTraversable = false
                    }
                    viewButtonAppendTo = jfxButton("+") {
                        isFocusTraversable = false
                    }
                }
                hbox(spacing = 4) {
                    style {
                        padding = box(
                                top = 20.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                    viewTextFieldTicket = jfxTextField {
                        minWidth = 120.0
                        maxWidth = 120.0
                        focusColor = Styles.cActiveRed
                        isLabelFloat = true
                        promptText = "Ticket ID"
                        unFocusColor = Color.BLACK
                        textProperty().addListener { _, _, newValue ->
                            ticketInfoLoader.changeInputCode(newValue)
                        }
                    }
                    viewTextTicketDesc = label { }
                    viewButtonTicketLink = jfxButton {
                        isFocusTraversable = false
                        graphic = graphics.from(Glyph.LINK, Color.BLACK, 16.0, 20.0)
                        tooltip = Tooltip("Copy issue link to clipboard")
                        setOnAction {
                            val issue = viewTextFieldTicket.text.toString()
                            val issueLink = hostServicesInteractor.generateLink(issue)
                            eventBus.post(EventSnackBarMessage("Copied $issueLink to clipboard"))
                            hostServicesInteractor.copyLinkToClipboard(issue)
                        }
                    }
                    viewButtonSearch = jfxButton {
                        isFocusTraversable = false
                        graphic = graphics.from(Glyph.SEARCH, Color.BLACK, 20.0)
                        setOnAction {
                            eventBus.post(EventInflateDialog(DialogType.TICKET_SEARCH))
                        }
                    }
                }
                hbox {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    style {
                        padding = box(
                                top = 20.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                    viewTextComment = jfxTextArea {
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                        focusColor = Styles.cActiveRed
                        isLabelFloat = true
                        promptText = "Comment"
                        prefRowCount = 5
                    }
                }
                hbox(alignment = Pos.CENTER) {
                    hgrow = Priority.ALWAYS
                    style {
                        padding = box(
                                top = 10.px,
                                left = 0.px,
                                right = 0.px,
                                bottom = 0.px
                        )
                    }
                    viewLabelHint = label { addClass(Styles.labelMini) }
                    viewLabelHint2 = label { addClass(Styles.labelMini) }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                viewButtonSave = jfxButton("Save") {
                    setOnAction {
                        logEditService.saveEntity(
                                start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
                                end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
                                task = viewTextFieldTicket.text,
                                comment = viewTextComment.text
                        )
                    }
                }
                viewButtonDismiss = jfxButton("Dismiss") {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        viewTextTicketDesc.text = ""
        uiBridgeTimeQuickEdit = UIBridgeTimeQuickEdit(
                viewButtonSubtractFrom,
                viewButtonSubtractTo,
                viewButtonAppendFrom,
                viewButtonAppendTo,
                viewDatePickerFrom,
                viewTimePickerFrom,
                viewDatePickerTo,
                viewTimePickerTo,
                logEditService,
                timeProvider
        )
        uiBridgeDateTimeHandler = UIBridgeDateTimeHandler(
                jfxDateFrom = viewDatePickerFrom,
                jfxTimeFrom = viewTimePickerFrom,
                jfxDateTo = viewDatePickerTo,
                jfxTimeTo = viewTimePickerTo,
                timeProvider = timeProvider,
                clockEditPresenter = null,
                logEditService = logEditService
        )
        val entity: SimpleLog? = resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)
        if (entity != null) {
            logEditService.serviceType = LogEditService.ServiceType.UPDATE
            logEditService.entityInEdit = entity
            viewLabelHeader.text = "Log details"
            viewButtonSave.text = "UPDATE"
            viewButtonSave.graphic = graphics.from(Glyph.UPDATE, Color.BLACK, 12.0)
        } else {
            logEditService.serviceType = LogEditService.ServiceType.CREATE
            viewLabelHeader.text = "New log details"
            viewButtonSave.text = "CREATE"
            viewButtonSave.graphic = graphics.from(Glyph.NEW, Color.BLACK, 12.0)
        }
        logEditService.redraw()
        uiBridgeDateTimeHandler.onAttach()
        eventBus.register(this)
        ticketInfoLoader.onAttach()
        presenter.onAttach(this)
    }

    override fun onUndock() {
        presenter.onDetach()
        ticketInfoLoader.onDetach()
        eventBus.unregister(this)
        uiBridgeDateTimeHandler.onDetach()
        super.onUndock()
    }

    @Subscribe
    fun eventSuggestTicket(eventSuggestTicket: EventSuggestTicket) {
        viewTextFieldTicket.text = eventSuggestTicket.ticket.code.code
    }

    companion object {
        const val RESULT_DISPATCH_KEY_ENTITY = "VEggqIVId1"
        private val logger = LoggerFactory.getLogger(LogDetailsWidget::class.java)!!
    }

}