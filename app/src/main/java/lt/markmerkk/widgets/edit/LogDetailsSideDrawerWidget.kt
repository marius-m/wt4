package lt.markmerkk.widgets.edit

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import com.vdurmont.emoji.EmojiParser
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TicketUseHistory
import lt.markmerkk.events.*
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.RecentTicketLoader
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler
import lt.markmerkk.ui_2.bridges.UIBridgeDateTimeHandler2
import lt.markmerkk.ui_2.bridges.UIBridgeTimeQuickEdit
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.utils.AccountAvailablility
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.JiraLinkGeneratorBasic
import lt.markmerkk.utils.JiraLinkGeneratorOAuth
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.views.JFXScrollFreeTextArea
import lt.markmerkk.widgets.tickets.RecentTicketViewModel
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

class LogDetailsSideDrawerWidget : Fragment(),
        LogDetailsContract.View,
        JiraLinkGenerator.View,
        RecentTicketLoader.Listener {

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
//    private lateinit var viewTimePickerFrom: JFXTimePicker
//    private lateinit var viewTimeComboPickerFrom: JFXComboBox<java.time.LocalTime>
    private lateinit var viewButtonSubtractFrom: JFXButton
    private lateinit var viewButtonAppendFrom: JFXButton
    private lateinit var viewDatePickerTo: JFXDatePicker
//    private lateinit var viewTimePickerTo: JFXTimePicker
//    private lateinit var viewTimeComboPickerTo: JFXComboBox<java.time.LocalTime>
//    private lateinit var viewButtonSubtractTo: JFXButton
//    private lateinit var viewButtonAppendTo: JFXButton
    private lateinit var viewTextFieldTicket: JFXTextField
    private lateinit var viewTextTicketDesc: Label
    private lateinit var viewButtonTicketLink: JFXButton
    private lateinit var viewButtonSearch: JFXButton
    private lateinit var viewTextComment: JFXScrollFreeTextArea
    private lateinit var viewButtonSave: JFXButton
    private lateinit var viewButtonClose: JFXButton
    private lateinit var viewLabelHint: Label
    private lateinit var viewLabelHint2: Label
    private lateinit var viewTableRecent: TableView<RecentTicketViewModel>

//    private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
//    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
//    private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler2
    private lateinit var presenter: LogDetailsContract.Presenter
    private lateinit var ticketInfoLoader: TicketInfoLoader
    private lateinit var jiraLinkGenerator: JiraLinkGenerator

    private val recentTicketViewModels = mutableListOf<RecentTicketViewModel>().asObservable()
    private val timeSelectFrom = mutableListOf<String>().asObservable()
    private val timeSelectTo = mutableListOf<String>().asObservable()

    init {
        Main.component().inject(this)
    }

    private val contextMenuTicketSelect: ContextMenuTicketSelect = ContextMenuTicketSelect(
            graphics = graphics,
            eventBus = eventBus,
            hostServicesInteractor = hostServicesInteractor,
            accountAvailablility = accountAvailablility
    )
    private val recentTicketLoader = RecentTicketLoader(
            listener = this,
            ticketStorage = ticketStorage,
            ioScheduler = schedulerProvider.io(),
            uiScheduler = schedulerProvider.ui()
    )

    override val root: Parent = stackpane {
        setOnKeyReleased { keyEvent ->
            if (keyEvent.code == KeyCode.ENTER) {
                if (viewTableRecent.isFocused) {
                    val selectRecentTicket = viewTableRecent
                            .selectionModel
                            .selectedItems
                            .firstOrNull()?.ticketUseHistory
                    if (selectRecentTicket != null) {
                        val ticket = Ticket.new(
                                code = selectRecentTicket.code.code,
                                description = selectRecentTicket.description,
                                remoteData = null
                        )
                        eventBus.post(EventSuggestTicket(ticket))
                        eventBus.post(EventMainCloseTickets())
                        focusInput()
                    }
                }
            }
        }
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
                            maxWidth = 120.0
                            isFocusTraversable = false
                            isOverLay = true
                            defaultColor = Styles.cActiveRed
                        }
                        viewButtonSubtractFrom = jfxButton {
                            graphic = graphics.from(Glyph.ARROW_REWIND, Color.BLACK, 10.0)
                            isFocusTraversable = false
                        }
//                        viewTimeComboPickerFrom = jfxCombobox {
//                            maxWidth = 90.0
//                            isFocusTraversable = false
//                        }
                        viewButtonAppendFrom = jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FORWARD, Color.BLACK, 10.0)
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
                            maxWidth = 120.0
                            isFocusTraversable = false
                            isOverLay = true
                            defaultColor = Styles.cActiveRed
                        }
//                        viewTimeComboPickerTo = jfxCombobox {
//                            maxWidth = 100.0
//                            isFocusTraversable = false
//                        }
//                        viewButtonSubtractTo = jfxButton("-") {
//                            isFocusTraversable = false
//                        }
//                        viewButtonAppendTo = jfxButton("+") {
//                            isFocusTraversable = false
//                        }
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
                                eventBus.post(EventMainOpenTickets())
                            }
                            shortcut(KeyCombination.valueOf("Ctrl+f"))
                            shortcut(KeyCombination.valueOf("Meta+f"))
                        }
                    }
                    viewTableRecent = tableview(recentTicketViewModels) {
                        minHeight = 120.0
                        prefHeight = 180.0
                        maxHeight = 240.0
                        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                        contextMenu = contextMenuTicketSelect.root
                        hgrow = Priority.ALWAYS
                        setOnMouseClicked { mouseEvent ->
                            val selectRecentTicket = viewTableRecent
                                    .selectionModel
                                    .selectedItems
                                    .firstOrNull()?.ticketUseHistory
                            if (mouseEvent.clickCount >= 2 && selectRecentTicket != null) {
                                val ticket = Ticket.new(
                                        code = selectRecentTicket.code.code,
                                        description = selectRecentTicket.description,
                                        remoteData = null
                                )
                                eventBus.post(EventSuggestTicket(ticket))
                                eventBus.post(EventMainCloseTickets())
                                focusInput()
                            }
                        }
                        readonlyColumn("Code", RecentTicketViewModel::code) {
                            minWidth = 100.0
                            maxWidth = 100.0
                        }
                        readonlyColumn("Description", RecentTicketViewModel::description) { }
                        readonlyColumn("Last used", RecentTicketViewModel::lastUsed) {
                            minWidth = 100.0
                            maxWidth = 100.0
                        }
                        hide()
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
                                    hgrow = Priority.ALWAYS
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
//                            presenter.save(
//                                    start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
//                                    end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
//                                    task = viewTextFieldTicket.text,
//                                    comment = viewTextComment.textArea.text
//                            )
                        }
                    }
                    viewButtonClose = jfxButton("Close".toUpperCase()) {
                        setOnAction {
                            eventBus.post(EventMainCloseLogDetails())
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
                        ticketStorage,
                        userSettings
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
//        uiBridgeTimeQuickEdit = UIBridgeTimeQuickEdit(
//                viewButtonSubtractFrom,
//                viewButtonSubtractTo,
//                viewButtonAppendFrom,
//                viewButtonAppendTo,
//                viewDatePickerFrom,
//                viewTimePickerFrom,
//                viewDatePickerTo,
//                viewTimePickerTo,
//                object : UIBridgeTimeQuickEdit.DateTimeUpdater {
//                    override fun updateDateTime(start: DateTime, end: DateTime) {
//                        presenter.changeDateTime(start, end)
//                    }
//                },
//                timeProvider
//        )
//        uiBridgeDateTimeHandler = UIBridgeDateTimeHandler2(
//                jfxDateFrom = viewDatePickerFrom,
//                jfxTimeFrom = viewTimeComboPickerFrom,
//                jfxDateTo = viewDatePickerTo,
//                jfxTimeTo = viewTimeComboPickerTo,
//                timeProvider = timeProvider,
//                dateTimeUpdater = object : UIBridgeDateTimeHandler2.DateTimeUpdater {
//                    override fun updateDateTime(start: DateTime, end: DateTime) {
//                        presenter.changeDateTime(start, end)
//                    }
//                }
//        )
        contextMenuTicketSelect.onAttach()
        contextMenuTicketSelect.attachTicketSelection(
                JavaFxObservable.valuesOf(viewTableRecent.selectionModel.selectedItemProperty())
                        .filter { it != null }
                        .map { it.code }
        )
        recentTicketLoader.onAttach()
        presenter.onAttach(this)
//        uiBridgeDateTimeHandler.onAttach()
        eventBus.register(this)
        ticketInfoLoader.onAttach()
        ticketInfoLoader.attachInputCodeAsStream(JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty()))
        JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty())
                .subscribe({
                    presenter.changeTicketCode(it)
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        jiraLinkGenerator.onAttach()
        jiraLinkGenerator.attachTicketCodeInput(JavaFxObservable.valuesOf(viewTextFieldTicket.textProperty()))
        jiraLinkGenerator.handleTicketInput(viewTextFieldTicket.text.toString())
        JavaFxObservable.valuesOf(viewTextComment.textArea.textProperty())
                .subscribe({
                    presenter.changeComment(it)
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        JavaFxObservable.valuesOf(viewTableRecent.focusedProperty())
                .subscribe({
                    handleRecentVisibility()
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        JavaFxObservable.valuesOf(viewTextFieldTicket.focusedProperty())
                .subscribe({
                    handleRecentVisibility()
                }, { error ->
                    logger.warn("JFX prop error", error)
                })

        recentTicketLoader.fetch()
        Platform.runLater {
            viewTextComment.textArea.requestFocus()
            viewTextComment.textArea.positionCaret(viewTextComment.textArea.text.length)
        }
    }

    override fun onUndock() {
        contextMenuTicketSelect.onDetach()
        recentTicketLoader.onDetach()
        logger.debug("LogDetails:onUndock()")
        jiraLinkGenerator.onDetach()
        ticketInfoLoader.onDetach()
        eventBus.unregister(this)
//        uiBridgeDateTimeHandler.onDetach()
        presenter.onDetach()
        super.onUndock()
    }

    override fun initView(
            labelHeader: String,
            labelButtonSave: String,
            glyphButtonSave: SVGGlyph?,
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
//        uiBridgeDateTimeHandler.changeDate(initDateTimeStart, initDateTimeEnd)
        if (enableFindTickets) {
            viewButtonSearch.show()
        } else {
            viewButtonSearch.hide()
        }
        if (enableDateTimeChange) {
            viewDatePickerFrom.isDisable = false
//            viewTimeComboPickerFrom.isDisable = false
            viewDatePickerTo.isDisable = false
//            viewTimeComboPickerTo.isDisable = false
        } else {
            viewDatePickerFrom.isDisable = true
//            viewTimeComboPickerFrom.isDisable = true
            viewDatePickerTo.isDisable = true
//            viewTimeComboPickerTo.isDisable = true
        }
        ticketInfoLoader.findTicket(initTicket)
    }

    //register Events

    @Subscribe
    fun onFocusLogDetailsWidget(event: EventFocusLogDetailsWidget) {
        viewTextComment.textArea.requestFocus()
        viewTextComment.textArea.positionCaret(viewTextComment.textArea.text.length)
    }

    @Subscribe
    fun eventSuggestTicket(eventSuggestTicket: EventSuggestTicket) {
        viewTextFieldTicket.text = eventSuggestTicket.ticket.code.code
    }

    @Subscribe
    fun eventSave(event: EventLogDetailsSave) {
//        presenter.save(
//                start = timeProvider.toJodaDateTime(viewDatePickerFrom.value, viewTimePickerFrom.value),
//                end = timeProvider.toJodaDateTime(viewDatePickerTo.value, viewTimePickerTo.value),
//                task = viewTextFieldTicket.text,
//                comment = viewTextComment.textArea.text
//        )
    }

    //endregion

    override fun showDateTime(start: DateTime, end: DateTime) {
//        uiBridgeDateTimeHandler.changeDate(start, end)
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
//        uiBridgeDateTimeHandler.enable()
//        uiBridgeTimeQuickEdit.enable()
    }

    override fun disableInput() {
        viewTextFieldTicket.isEditable = false
        viewTextComment.textArea.isEditable = false
//        uiBridgeDateTimeHandler.disable()
//        uiBridgeTimeQuickEdit.disable()
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
            val message = EmojiParser.parseToUnicode("Copied $webLink :rocket:")
            eventBus.post(EventSnackBarMessage(message))
            hostServicesInteractor.ticketWebLinkToClipboard(webLink)
        }
    }

    override fun hideCopyLink() {
        viewButtonTicketLink.isDisable = true
        viewButtonTicketLink.setOnAction { }
    }

    override fun closeDetails() {
        eventBus.post(EventMainCloseLogDetails())
    }

    override fun onRecentTickets(tickets: List<TicketUseHistory>) {
        val now = timeProvider.preciseNow()
        val ticketsVm = tickets
                .map { RecentTicketViewModel(now, it) }
        this.recentTicketViewModels.clear()
        this.recentTicketViewModels.addAll(ticketsVm)
    }

    private fun handleRecentVisibility() {
        if (viewTableRecent.isFocused || viewTextFieldTicket.isFocused) {
            viewTableRecent.show()
        } else {
            viewTableRecent.hide()
        }
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