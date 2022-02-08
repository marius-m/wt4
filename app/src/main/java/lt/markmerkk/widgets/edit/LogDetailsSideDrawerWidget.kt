package lt.markmerkk.widgets.edit

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import com.vdurmont.emoji.EmojiParser
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.datepick.DateSelectRequest
import lt.markmerkk.datepick.DateSelectResult
import lt.markmerkk.datepick.DateSelectType
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TicketUseHistory
import lt.markmerkk.entities.TimeRangeRaw.Companion.withEndTime
import lt.markmerkk.entities.TimeRangeRaw.Companion.withStartDate
import lt.markmerkk.entities.TimeRangeRaw.Companion.withStartTime
import lt.markmerkk.entities.TimeRangeRaw.Companion.withEndDate
import lt.markmerkk.events.*
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.RecentTicketLoader
import lt.markmerkk.tickets.TicketInfoLoader
import lt.markmerkk.timeselect.entities.TimeSelectRequest
import lt.markmerkk.timeselect.entities.TimeSelectResult
import lt.markmerkk.timeselect.entities.TimeSelectType
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.utils.AccountAvailablility
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.JiraLinkGeneratorBasic
import lt.markmerkk.utils.JiraLinkGeneratorOAuth
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.views.JFXScrollFreeTextArea
import lt.markmerkk.widgets.datepicker.DatePickerWidget
import lt.markmerkk.widgets.timepicker.TimePickerWidget
import lt.markmerkk.widgets.tickets.RecentTicketViewModel
import lt.markmerkk.widgets.wrapAsSource
import org.joda.time.DateTime
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
    private lateinit var viewDatePickerFrom: JFXTextField
    private lateinit var viewTimePickerFrom: JFXTextField
    private lateinit var viewDatePickerTo: JFXTextField
    private lateinit var viewTimePickerTo: JFXTextField
    private lateinit var viewDuration: JFXTextField
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
    private lateinit var viewsDateTimeRangeElements: List<TextInputControl>

    // private lateinit var uiBridgeTimeQuickEdit: UIBridgeTimeQuickEdit
    // private lateinit var uiBridgeDateTimeHandler: UIBridgeDateTimeHandler
    private lateinit var presenter: LogDetailsContract.Presenter
    private lateinit var ticketInfoLoader: TicketInfoLoader
    private lateinit var jiraLinkGenerator: JiraLinkGenerator
    private lateinit var timeRangeGenerator: TimeRangeGenerator

    private val recentTicketViewModels = mutableListOf<RecentTicketViewModel>().asObservable()

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
                vbox(spacing = 10) {
                    label("Time range") {
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
                    hbox(spacing = 4, alignment = Pos.CENTER_LEFT) {
                        viewDatePickerFrom = jfxTextField {
                            minWidth = 100.0
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            isFocusTraversable = true
                            isEditable = false
                            focusColor = Styles.cActiveRed
                            isLabelFloat = false
                            promptText = ""
                            unFocusColor = Color.BLACK
                            text = ""
                            setOnMouseClicked {
                                val datePreselect = LogFormatters.dateFromRawOrDefault(viewDatePickerFrom.text)
                                resultDispatcher.publish(
                                    key = DatePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
                                    resultEntity = DateSelectRequest(
                                        dateSelection = datePreselect,
                                        extra = DateSelectType.SELECT_FROM.name
                                    )
                                )
                                find<DatePickerWidget>().openModal(
                                    stageStyle = StageStyle.DECORATED,
                                    block = true,
                                    resizable = false
                                )
                            }
                        }
                        viewTimePickerFrom = jfxTextField {
                            minWidth = 60.0
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            isFocusTraversable = true
                            isEditable = false
                            focusColor = Styles.cActiveRed
                            isLabelFloat = false
                            promptText = ""
                            unFocusColor = Color.BLACK
                            text = ""
                            setOnMouseClicked {
                                resultDispatcher.publish(
                                    key = TimePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
                                    resultEntity = TimeSelectRequest.asTimeFrom(
                                        timeSelection = LogFormatters.timeFromRawOrDefault(viewTimePickerFrom.text)
                                    )
                                )
                                find<TimePickerWidget>().openModal(
                                    stageStyle = StageStyle.DECORATED,
                                    modality = Modality.APPLICATION_MODAL,
                                    block = false,
                                    resizable = false
                                )
                            }
                        }
                        label("-")
                        viewDatePickerTo = jfxTextField {
                            minWidth = 100.0
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            isFocusTraversable = true
                            isEditable = false
                            focusColor = Styles.cActiveRed
                            isLabelFloat = false
                            promptText = ""
                            unFocusColor = Color.BLACK
                            text = ""
                            setOnMouseClicked {
                                val datePreselect = LogFormatters.dateFromRawOrDefault(viewDatePickerTo.text)
                                resultDispatcher.publish(
                                    key = DatePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
                                    resultEntity = DateSelectRequest(
                                        dateSelection = datePreselect,
                                        extra = DateSelectType.SELECT_TO.name
                                    )
                                )
                                find<DatePickerWidget>().openModal(
                                    stageStyle = StageStyle.DECORATED,
                                    block = true,
                                    resizable = false
                                )
                            }
                        }
                        viewTimePickerTo = jfxTextField {
                            minWidth = 60.0
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            isFocusTraversable = true
                            isEditable = false
                            focusColor = Styles.cActiveRed
                            isLabelFloat = false
                            promptText = ""
                            unFocusColor = Color.BLACK
                            text = ""
                            setOnMouseClicked {
                                resultDispatcher.publish(
                                    key = TimePickerWidget.RESULT_DISPATCH_KEY_PRESELECT,
                                    resultEntity = TimeSelectRequest.asTimeTo(
                                        timeSelection = LogFormatters.timeFromRawOrDefault(viewTimePickerTo.text)
                                    )
                                )
                                find<TimePickerWidget>().openModal(
                                    stageStyle = StageStyle.DECORATED,
                                    modality = Modality.APPLICATION_MODAL,
                                    block = false,
                                    resizable = false
                                )
                            }
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
                            val timeRange = timeRangeGenerator.generateTimeRange()
                            presenter.save(
                                    start = timeRange.dtStart,
                                    end = timeRange.dtEnd,
                                    task = viewTextFieldTicket.text,
                                    comment = viewTextComment.textArea.text
                            )
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
    }.let { stackPane ->
        viewsDateTimeRangeElements = listOf(
            viewDatePickerFrom,
            viewTimePickerFrom,
            viewDatePickerTo,
            viewTimePickerTo
        )
        stackPane
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
        timeRangeGenerator = object : TimeRangeGenerator {
            override val startDateSource: TimeRangeGenerator.Source = viewDatePickerFrom.wrapAsSource()
            override val startTimeSource: TimeRangeGenerator.Source = viewTimePickerFrom.wrapAsSource()
            override val endDateSource: TimeRangeGenerator.Source = viewDatePickerTo.wrapAsSource()
            override val endTimeSource: TimeRangeGenerator.Source = viewTimePickerTo.wrapAsSource()
        }
        viewTextTicketDesc.text = ""
        contextMenuTicketSelect.onAttach()
        contextMenuTicketSelect.attachTicketSelection(
                JavaFxObservable.valuesOf(viewTableRecent.selectionModel.selectedItemProperty())
                        .filter { it != null }
                        .map { it.code }
        )
        recentTicketLoader.onAttach()
        presenter.onAttach(this)
        // uiBridgeDateTimeHandler.onAttach()
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
        if (enableFindTickets) {
            viewButtonSearch.show()
        } else {
            viewButtonSearch.hide()
        }
        if (enableDateTimeChange) {
            viewsDateTimeRangeElements
                .forEach { it.isDisable = false }
        } else {
            viewsDateTimeRangeElements
                .forEach { it.isDisable = true }
        }
        ticketInfoLoader.findTicket(initTicket)
    }

    //region Events

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
        val timeRange = timeRangeGenerator.generateTimeRange()
        presenter.save(
            start = timeRange.dtStart,
            end = timeRange.dtEnd,
            task = viewTextFieldTicket.text,
            comment = viewTextComment.textArea.text
        )
    }

    @Subscribe
    fun eventChangeTime(event: EventChangeTime) {
        val timeSelectResult = resultDispatcher.consume(
            TimePickerWidget.RESULT_DISPATCH_KEY_RESULT,
            TimeSelectResult::class.java
        )
        if (timeSelectResult != null) {
            val timeSelectType = TimeSelectType.fromRaw(timeSelectResult.extra)
            when (timeSelectType) {
                TimeSelectType.UNKNOWN -> {}
                TimeSelectType.FROM -> {
                    val timeRange = timeRangeGenerator.generateTimeRange()
                        .withStartTime(timeSelectResult.timeSelectionNew)
                    presenter.changeDateTimeRaw(timeRange)
                }
                TimeSelectType.TO -> {
                    val timeRange = timeRangeGenerator.generateTimeRange()
                        .withEndTime(timeSelectResult.timeSelectionNew)
                    presenter.changeDateTimeRaw(timeRange)
                }
            }.javaClass
        }
    }

    @Subscribe
    fun eventChangeDate(event: EventChangeDate) {
        val dateSelectResult = resultDispatcher.peek(
            DatePickerWidget.RESULT_DISPATCH_KEY_RESULT,
            DateSelectResult::class.java
        )
        if (dateSelectResult != null) {
            val dateSelectType = DateSelectType.fromRaw(dateSelectResult.extra)
            when (dateSelectType) {
                DateSelectType.UNKNOWN,
                DateSelectType.TARGET_DATE -> {}
                DateSelectType.SELECT_FROM -> {
                    resultDispatcher.consume(DatePickerWidget.RESULT_DISPATCH_KEY_RESULT)
                    val timeRange = timeRangeGenerator.generateTimeRange()
                        .withStartDate(dateSelectResult.dateSelectionNew)
                    presenter.changeDateTimeRaw(timeRange)
                }
                DateSelectType.SELECT_TO -> {
                    resultDispatcher.consume(DatePickerWidget.RESULT_DISPATCH_KEY_RESULT)
                    val timeRange = timeRangeGenerator.generateTimeRange()
                        .withEndDate(dateSelectResult.dateSelectionNew)
                    presenter.changeDateTimeRaw(timeRange)
                }
            }.javaClass
        }
    }

    //endregion

    override fun showDateTime(start: DateTime, end: DateTime) {
        viewDatePickerFrom.text = LogFormatters.shortFormatDate.print(start)
        viewTimePickerFrom.text = LogFormatters.shortFormat.print(start)
        viewDatePickerTo.text = LogFormatters.shortFormatDate.print(end)
        viewTimePickerTo.text = LogFormatters.shortFormat.print(end)
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
        viewsDateTimeRangeElements
            .forEach { it.isEditable = true }
    }

    override fun disableInput() {
        viewTextFieldTicket.isEditable = false
        viewTextComment.textArea.isEditable = false
        viewsDateTimeRangeElements
            .forEach { it.isEditable = false }
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