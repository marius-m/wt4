package lt.markmerkk.widgets.edit

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.utils.AccountAvailablility
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.JiraLinkGeneratorBasic
import lt.markmerkk.utils.JiraLinkGeneratorOAuth
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.widgets.tickets.TicketWidget
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

@Deprecated("Replaced with LogDetailsSideDrawerWidget")
class LogDetailsWidget : Fragment(), LogDetailsContract.View, JiraLinkGenerator.View {

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var ticketStorage: TicketStorage
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var activeLogPersistence: ActiveLogPersistence
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var autoSyncWatcher: AutoSyncWatcher2
    @Inject lateinit var accountAvailablility: AccountAvailablility

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
    private lateinit var viewButtonClose: JFXButton
    private lateinit var viewLabelHint: Label
    private lateinit var viewLabelHint2: Label

    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
    private lateinit var presenter: LogDetailsContract.Presenter
    private lateinit var ticketInfoLoader: TicketInfoLoader
    private lateinit var jiraLinkGenerator: JiraLinkGenerator

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        setOnKeyPressed { keyEvent ->
            when {
                (keyEvent.code == KeyCode.ENTER && keyEvent.isMetaDown)
                        || (keyEvent.code == KeyCode.ENTER && keyEvent.isControlDown) -> {
                    presenter.save(
                            start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
                            end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
                            task = viewTextFieldTicket.text,
                            comment = viewTextComment.text
                    )
                }
                (keyEvent.code == KeyCode.SLASH && keyEvent.isMetaDown)
                        || (keyEvent.code == KeyCode.SLASH && keyEvent.isControlDown)
                        || (keyEvent.code == KeyCode.F && keyEvent.isMetaDown)
                        || (keyEvent.code == KeyCode.F && keyEvent.isControlDown) -> {
                    presenter.openFindTickets()
                }
            }
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
                        }
                        viewTextTicketDesc = label {
                            addClass(Styles.labelRegular)
                        }
                        viewButtonTicketLink = jfxButton {
                            isFocusTraversable = false
                            graphic = graphics.from(Glyph.LINK, Color.BLACK, 16.0, 20.0)
                            tooltip = Tooltip("Copy issue link to clipboard")
                            isDisable = true
                        }
                        viewButtonSearch = jfxButton {
                            isFocusTraversable = false
                            graphic = graphics.from(Glyph.SEARCH, Color.BLACK, 20.0)
                            setOnAction {
                                find<TicketWidget>().openWindow(
                                        block = true,
                                        stageStyle = StageStyle.DECORATED,
                                        modality = Modality.WINDOW_MODAL
                                )
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
                            textProperty().addListener { _, _, newValue ->
                                presenter.changeComment(newValue)
                            }
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
                    viewButtonClose = jfxButton("Close".toUpperCase()) {
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
        val isActiveClock = resultDispatcher.consumeBoolean(RESULT_DISPATCH_KEY_ACTIVE_CLOCK, false)
        val entity: SimpleLog? = resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)
        presenter = if (entity != null) {
            LogDetailsPresenterUpdate(
                    entity,
                    logStorage,
                    eventBus,
                    graphics,
                    timeProvider,
                    ticketStorage
            )
        } else {
            when  {
                isActiveClock -> LogDetailsPresenterUpdateActiveClock(
                        logStorage,
                        eventBus,
                        graphics,
                        timeProvider,
                        hourGlass,
                        activeLogPersistence,
                        ticketStorage
                )
                else -> LogDetailsPresenterCreate(
                        logStorage,
                        eventBus,
                        graphics,
                        timeProvider,
                        ticketStorage
                )
            }
        }
        ticketInfoLoader = TicketInfoLoader(
                listener = object : TicketInfoLoader.Listener {
                    override fun onTicketFound(ticket: Ticket) {
                        viewTextTicketDesc.text = ticket.description
                    }

                    override fun onNoTicket(searchTicket: String) {
                        viewTextTicketDesc.text = ""
                    }
                },
                ticketStorage = ticketStorage,
                waitScheduler = schedulerProvider.waitScheduler(),
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        jiraLinkGenerator = if (BuildConfig.oauth) {
            JiraLinkGeneratorOAuth(
                    view = this,
                    accountAvailability = accountAvailablility
            )
        } else {
            JiraLinkGeneratorBasic(
                    view = this,
                    accountAvailablility = accountAvailablility
            )
        }
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
        ticketInfoLoader.attachInputCodeAsStream(JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty()))
        JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty())
                .subscribe { presenter.changeTicketCode(it) }
        viewTextComment.requestFocus()
        viewTextComment.positionCaret(viewTextComment.text.length)
        jiraLinkGenerator.onAttach()
        jiraLinkGenerator.attachTicketCodeInput(JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty()))
        jiraLinkGenerator.handleTicketInput(viewTextFieldTicket.text.toString())
        autoSyncWatcher.changeUpdateLock(isInLock = true, lockProcessName = "LogDetails")
    }

    override fun onUndock() {
        autoSyncWatcher.changeUpdateLock(isInLock = false, lockProcessName = "")
        jiraLinkGenerator.onDetach()
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
            initDateTimeEnd: DateTime,
            initTicket: String,
            initComment: String,
            enableFindTickets: Boolean,
            enableDateTimeChange: Boolean
    ) {
        viewLabelHeader.text = labelHeader
        viewButtonSave.text = labelButtonSave.toUpperCase()
        viewButtonSave.graphic = glyphButtonSave
        viewTextFieldTicket.text = initTicket
        viewTextComment.text = initComment
        uiBridgeDateTimeHandler.changeDate(initDateTimeStart, initDateTimeEnd)
        if (enableFindTickets) {
            viewButtonSearch.show()
        } else {
            viewButtonSearch.hide()
        }
        if (enableDateTimeChange) {
            viewDatePickerFrom.isDisable = false
            viewTimePickerFrom.isDisable = false
            viewDatePickerTo.isDisable = false
            viewTimePickerTo.isDisable = false
        } else {
            viewDatePickerFrom.isDisable = true
            viewTimePickerFrom.isDisable = true
            viewDatePickerTo.isDisable = true
            viewTimePickerTo.isDisable = true
        }
        ticketInfoLoader.findTicket(initTicket)
    }

    override fun showDateTime(start: DateTime, end: DateTime) {
        uiBridgeDateTimeHandler.changeDate(start, end)
    }

    override fun showTicketCode(ticket: String) {
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

    override fun showCopyLink(ticketCode: TicketCode, webLink: String) {
        viewButtonTicketLink.isDisable = false
        viewButtonTicketLink.setOnAction {
            hostServicesInteractor.ticketWebLinkToClipboard(webLink)
        }
    }

    override fun hideCopyLink() {
        viewButtonTicketLink.isDisable = true
        viewButtonTicketLink.setOnAction { }
    }

    override fun closeDetails() {
        close()
    }

    @Subscribe
    fun eventSuggestTicket(eventSuggestTicket: EventSuggestTicket) {
        viewTextFieldTicket.text = eventSuggestTicket.ticket.code.code
    }

    companion object {
        const val RESULT_DISPATCH_KEY_ENTITY = "732b4810-2a44-4aee-b63d-da8252e1f1a0"
        const val RESULT_DISPATCH_KEY_ACTIVE_CLOCK = "78c9df52-c092-4e87-aaad-67c9b3b43977"
        private val logger = LoggerFactory.getLogger(LogDetailsWidget::class.java)!!
    }

}