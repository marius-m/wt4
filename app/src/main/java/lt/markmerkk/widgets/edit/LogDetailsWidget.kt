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
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.ui_2.views.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class LogDetailsWidget : View(), LogDetailsContract.View {

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
    private lateinit var presenter: LogDetailsContract.Presenter
    private lateinit var ticketInfoLoader: TicketInfoLoader

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        setOnKeyPressed { keyEvent ->
//            if (!logEditService.canEdit()) {
//                logger.debug("Cannot edit service")
//            } else {
//                if ((keyEvent.code == KeyCode.ENTER && keyEvent.isMetaDown)
//                        || (keyEvent.code == KeyCode.ENTER && keyEvent.isControlDown)) {
//                    logEditService.saveEntity(
//                            start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
//                            end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
//                            task = viewTextFieldTicket.text,
//                            comment = viewTextComment.text
//                    )
//                }
//            }
        }
        borderpane {
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
                    hbox(spacing = 4, alignment = Pos.CENTER_LEFT) {
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
                        viewTextTicketDesc = label {
                            addClass(Styles.labelRegular)
                        }
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
                    viewButtonSave = jfxButton("Save".toUpperCase()) {
                        setOnAction {
                            presenter.save(
                                    start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
                                    end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
                                    task = viewTextFieldTicket.text,
                                    comment = viewTextComment.text
                            )
                        }
                    }
                    viewButtonDismiss = jfxButton("Dismiss".toUpperCase()) {
                        setOnAction {
                            close()
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        val entity: SimpleLog? = resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)
        presenter = if (entity != null) {
            LogDetailsPresenterUpdate(
                    entity,
                    logStorage,
                    hostServicesInteractor,
                    eventBus,
                    graphics,
                    ticketsDatabaseRepo,
                    resultDispatcher,
                    schedulerProvider,
                    timeProvider
            )
        } else {
            LogDetailsPresenterCreate(
                    logStorage,
                    hostServicesInteractor,
                    eventBus,
                    graphics,
                    ticketsDatabaseRepo,
                    resultDispatcher,
                    schedulerProvider,
                    timeProvider
            )
        }
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
                object : UIBridgeTimeQuickEdit.DateTimeUpdater {
                    override fun updateDateTime(start: DateTime, end: DateTime) {
                        presenter.changeDateTime(start, end)
                    }
                },
                timeProvider
        )
        uiBridgeDateTimeHandler = UIBridgeDateTimeHandler(
                jfxDateFrom = viewDatePickerFrom,
                jfxTimeFrom = viewTimePickerFrom,
                jfxDateTo = viewDatePickerTo,
                jfxTimeTo = viewTimePickerTo,
                timeProvider = timeProvider,
                dateTimeUpdater = object : UIBridgeDateTimeHandler.DateTimeUpdater {
                    override fun updateDateTime(start: DateTime, end: DateTime) {
                        presenter.changeDateTime(start, end)
                    }
                }
        )
        presenter.onAttach(this)
        uiBridgeDateTimeHandler.onAttach()
        eventBus.register(this)
        ticketInfoLoader.onAttach()
        viewTextComment.requestFocus()
    }

    override fun onUndock() {
        ticketInfoLoader.onDetach()
        eventBus.unregister(this)
        uiBridgeDateTimeHandler.onDetach()
        presenter.onDetach()
        super.onUndock()
    }

    override fun initView(
            labelHeader: String,
            labelButtonSave: String,
            glyphButtonSave: SVGGlyph,
            initDateTimeStart: DateTime,
            initDateTimeEnd: DateTime
    ) {
        viewLabelHeader.text = labelHeader
        viewButtonSave.text = labelButtonSave.toUpperCase()
        viewButtonSave.graphic = glyphButtonSave
        uiBridgeDateTimeHandler.changeDate(initDateTimeStart, initDateTimeEnd)
    }

    override fun showDateTime(start: DateTime, end: DateTime) {
        uiBridgeDateTimeHandler.changeDate(start, end)
    }

    override fun showTicket(ticket: String) {
        viewTextFieldTicket.text = ticket
    }

    override fun showComment(comment: String) {
        viewTextComment.text = comment
    }

    override fun showHint1(hint: String) {
        viewLabelHint.text = hint
    }

    override fun showHint2(hint: String) {
        viewLabelHint2.text = hint
    }

    override fun enableInput() {
        viewTextFieldTicket.isEditable = true
        viewTextComment.isEditable = true
        uiBridgeDateTimeHandler.enable()
        uiBridgeTimeQuickEdit.enable()
    }

    override fun disableInput() {
        viewTextFieldTicket.isEditable = false
        viewTextComment.isEditable = false
        uiBridgeDateTimeHandler.disable()
        uiBridgeTimeQuickEdit.disable()
    }

    override fun enableSaving() {
        viewButtonSave.isDisable = false
    }

    override fun disableSaving() {
        viewButtonSave.isDisable = true
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