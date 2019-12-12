package lt.markmerkk.widgets.edit

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXTimePicker
import com.jfoenix.svg.SVGGlyph
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.events.*
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxDatePicker
import lt.markmerkk.ui_2.views.jfxTextField
import lt.markmerkk.ui_2.views.jfxTimePicker
import lt.markmerkk.utils.AccountAvailablility
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.JiraLinkGeneratorBasic
import lt.markmerkk.utils.JiraLinkGeneratorOAuth
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.views.JFXScrollFreeTextArea
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

class LogDetailsSideDrawerWidget : Fragment(), LogDetailsContract.View, JiraLinkGenerator.View {

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
    private lateinit var viewTextComment: JFXScrollFreeTextArea
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
        addClass(Styles.sidePanelContainer)
        borderpane {
            top {
                viewLabelHeader = label("Log details") {
                    addClass(Styles.sidePanelHeader)
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
                            maxWidth = 110.0
                            isFocusTraversable = false
                            isOverLay = true
                            defaultColor = Styles.cActiveRed
                        }
                        viewTimePickerFrom = jfxTimePicker {
                            maxWidth = 74.0
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
                            maxWidth = 110.0
                            isFocusTraversable = false
                            isOverLay = true
                            defaultColor = Styles.cActiveRed
                        }
                        viewTimePickerTo = jfxTimePicker {
                            maxWidth = 74.0
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
                        style { backgroundColor.add(Color.BLUE) }
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
                                eventBus.post(EventMainToggleTickets())
                            }
                            shortcut(KeyCombination.valueOf("Ctrl+f"))
                            shortcut(KeyCombination.valueOf("Meta+f"))
                        }
                    }
                    hbox {
                        hgrow = Priority.ALWAYS
                        style {
                            padding = box(
                                    top = 20.px,
                                    left = 0.px,
                                    right = 0.px,
                                    bottom = 0.px
                            )
                        }
                        viewTextComment = JFXScrollFreeTextArea()
                                .apply {
                                    textArea.focusColor = Styles.cActiveRed
                                    textArea.isLabelFloat = true
                                    textArea.promptText = "Comment"
                                }
                        add(viewTextComment)
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
                                    comment = viewTextComment.textArea.text
                            )
                        }
                    }
                    viewButtonClose = jfxButton("Close".toUpperCase()) {
                        setOnAction {
                            eventBus.post(EventMainToggleLogDetails())
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        logger.debug("LogDetails:onDock()")
        val isActiveClock = resultDispatcher.consumeBoolean(RESULT_DISPATCH_KEY_ACTIVE_CLOCK, false)
        val entity: SimpleLog? = resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)
        presenter = if (entity != null) {
            LogDetailsPresenterUpdate(
                    entity,
                    logStorage,
                    eventBus,
                    graphics,
                    timeProvider
            )
        } else {
            when  {
                isActiveClock -> LogDetailsPresenterUpdateActiveClock(
                        logStorage,
                        eventBus,
                        graphics,
                        timeProvider,
                        hourGlass,
                        activeLogPersistence
                )
                else -> LogDetailsPresenterCreate(
                        logStorage,
                        eventBus,
                        graphics,
                        timeProvider
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
        jiraLinkGenerator.onAttach()
        jiraLinkGenerator.attachTicketCodeInput(JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty()))
        jiraLinkGenerator.handleTicketInput(viewTextFieldTicket.text.toString())
        JavaFxObservable.valuesOf(viewTextComment.textArea.textProperty())
                .subscribe { presenter.changeComment(it) }

        Platform.runLater {
            viewTextComment.textArea.requestFocus()
            viewTextComment.textArea.positionCaret(viewTextComment.textArea.text.length)
        }
    }

    override fun onUndock() {
        logger.debug("LogDetails:onUndock()")
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
        viewTextComment.textArea.text = initComment
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

    //register Events

    @Subscribe
    fun eventSuggestTicket(eventSuggestTicket: EventSuggestTicket) {
        viewTextFieldTicket.text = eventSuggestTicket.ticket.code.code
    }

    @Subscribe
    fun eventSave(event: EventLogDetailsSave) {
        presenter.save(
                start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
                end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
                task = viewTextFieldTicket.text,
                comment = viewTextComment.textArea.text
        )
    }

    //endregion

    override fun showDateTime(start: DateTime, end: DateTime) {
        uiBridgeDateTimeHandler.changeDate(start, end)
    }

    override fun showTicketCode(ticket: String) {
        viewTextFieldTicket.text = ticket
    }

    override fun showComment(comment: String) {
        viewTextComment.textArea.text = comment
    }

    override fun showHint1(hint: String) {
        viewLabelHint.text = hint
    }

    override fun showHint2(hint: String) {
        viewLabelHint2.text = hint
    }

    override fun enableInput() {
        viewTextFieldTicket.isEditable = true
        viewTextComment.textArea.isEditable = true
        uiBridgeDateTimeHandler.enable()
        uiBridgeTimeQuickEdit.enable()
    }

    override fun disableInput() {
        viewTextFieldTicket.isEditable = false
        viewTextComment.textArea.isEditable = false
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
            eventBus.post(EventSnackBarMessage("Copy $webLink"))
            hostServicesInteractor.ticketWebLinkToClipboard(webLink)
        }
    }

    override fun hideCopyLink() {
        viewButtonTicketLink.isDisable = true
        viewButtonTicketLink.setOnAction { }
    }

    override fun closeDetails() {
        eventBus.post(EventMainToggleLogDetails())
    }

    fun focusInput() {
        viewTextComment.textArea.requestFocus()
        viewTextComment.textArea.positionCaret(viewTextComment.textArea.text.length)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LogDetailsSideDrawerWidget::class.java)!!
        const val RESULT_DISPATCH_KEY_ENTITY = "42344549-81f2-4c24-84cf-213e34b4932b"
        const val RESULT_DISPATCH_KEY_ACTIVE_CLOCK = "455aa6ed-e914-4b94-ab58-1f2855f6db6e"
    }

}