package lt.markmerkk.widgets.main

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.svg.SVGGlyph
import com.vdurmont.emoji.EmojiParser
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.events.*
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.ui_2.views.SideContainerLogDetails
import lt.markmerkk.ui_2.views.SideContainerTickets
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidget
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxDrawer
import lt.markmerkk.ui_2.views.progress.ProgressWidget
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import lt.markmerkk.utils.ConfigSetSettings
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.Ticker
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.versioner.VersionProvider
import lt.markmerkk.widgets.calendar.CalendarWidget
import lt.markmerkk.widgets.clock.ClockWidget
import lt.markmerkk.widgets.edit.LogDetailsSideDrawerWidget
import lt.markmerkk.widgets.log_check.LogFreshnessChecker
import lt.markmerkk.widgets.log_check.LogFreshnessWidget
import lt.markmerkk.widgets.settings.AccountSettingsOauthWidget
import lt.markmerkk.widgets.settings.AccountSettingsWidget
import lt.markmerkk.widgets.tickets.PopUpChangeMainContent
import lt.markmerkk.widgets.tickets.PopUpSettings
import lt.markmerkk.widgets.versioner.ChangelogWidget
import org.slf4j.LoggerFactory
import rx.Subscription
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

class MainWidget : Fragment(), MainContract.View {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var strings: Strings
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logChangeValidator: LogChangeValidator
    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var connProvider: DBConnProvider
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var autoSyncWatcher: AutoSyncWatcher2
    @Inject lateinit var jiraClientProvider: JiraClientProvider
    @Inject lateinit var versionProvider: VersionProvider
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var jiraLinkGenerator: JiraLinkGenerator
    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var ticker: Ticker
    @Inject lateinit var logFreshnessChecker: LogFreshnessChecker
    @Inject lateinit var configSetSettings: ConfigSetSettings
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository

    lateinit var jfxButtonDisplayView: JFXButton
    lateinit var jfxButtonSettings: JFXButton
    lateinit var viewSideDrawerLogDetails: JFXDrawer
    lateinit var viewSideDrawerTickets: JFXDrawer

    lateinit var snackBar: JFXSnackbar

    private var subsFocusChange: Subscription? = null

    private lateinit var presenter: MainContract.Presenter
    private lateinit var sidePaneHandler: SidePaneHandler

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        setOnKeyReleased { keyEvent ->
            if (keyEvent.code == KeyCode.ALT) {
                eventBus.post(EventEditMode(isInEdit = false))
            }
        }
        setOnKeyPressed { keyEvent ->
            if (keyEvent.code == KeyCode.ALT) {
                eventBus.post(EventEditMode(isInEdit = true))
            }
        }
        val actionRefresh = {
            syncInteractor.syncActiveTime()
            autoSyncWatcher.reset()
        }
        shortcut(KeyCombination.valueOf("Meta+R"), actionRefresh)
        shortcut(KeyCombination.valueOf("Ctrl+R"), actionRefresh)
        val actionLogDetails = {
            resultDispatcher.publish(LogDetailsSideDrawerWidget.RESULT_DISPATCH_KEY_ACTIVE_CLOCK, true)
            eventBus.post(EventMainOpenLogDetails())
        }
        shortcut(KeyCombination.valueOf("Meta+S"), actionLogDetails)
        shortcut(KeyCombination.valueOf("Ctrl+S"), actionLogDetails)
        shortcut(KeyCombination.valueOf("Esc")) {
            when {
                viewSideDrawerTickets.isOpened
                        || viewSideDrawerTickets.isOpening -> viewSideDrawerTickets.close()
                viewSideDrawerLogDetails.isOpened
                        || viewSideDrawerLogDetails.isOpening -> viewSideDrawerLogDetails.close()
            }
        }
        val actionSaveLog = {
            if (viewSideDrawerLogDetails.isOpened
                    || viewSideDrawerLogDetails.isOpening
                    && !viewSideDrawerTickets.isOpened) {
                eventBus.post(EventLogDetailsSave())
            }
        }
        shortcut(KeyCombination.valueOf("Meta+Enter"), actionSaveLog)
        shortcut(KeyCombination.valueOf("Ctrl+Enter"), actionSaveLog)
        val viewContainerMenu = vbox(spacing = 4, alignment = Pos.CENTER_LEFT) {
            StackPane.setAlignment(this, Pos.CENTER_LEFT)
            isFocusTraversable = false
            style {
                maxWidth = 60.0.px
                backgroundColor.add(Styles.cBackgroundPrimary)
                padding = box(vertical = 4.px, horizontal = 4.px)
            }
            add(find<ClockWidget>().root)
            vbox(spacing = 4, alignment = Pos.BOTTOM_CENTER) {
                hgrow = Priority.NEVER
                vgrow = Priority.ALWAYS
                jfxButtonDisplayView = jfxButton {
                    addClass(Styles.buttonMenu)
                    graphic = graphics.from(Glyph.DISPLAY_DAY, Color.WHITE, 20.0)
                    setOnAction {
                        PopUpChangeMainContent(graphics, eventBus, jfxButtonDisplayView)
                                .show()
                    }
                }
                jfxButtonSettings = jfxButton {
                    addClass(Styles.buttonMenu)
                    graphic = graphics.from(Glyph.SETTINGS, Color.WHITE, 20.0)
                    setOnAction {
                        PopUpSettings(graphics, jfxButtonSettings, hostServicesInteractor)
                                .show()
                    }
                }
            }
        }
        val viewContainerMain = borderpane {
            top {
                isFocusTraversable = false
                borderpane {
                    left {
                        hbox {
                            add(find<QuickDateChangeWidget>().root)
                            add(find<QuickEditContainerWidget>().root)
                        }
                    }
                    right {
                        hbox {
                            add(find<ProgressWidget>().root)
                        }
                    }
                }
            }
            center {
                isFocusTraversable = false
                val widgetCalendar = find<CalendarWidget>()
                add(widgetCalendar)
            }
        }
        viewSideDrawerLogDetails = jfxDrawer {
            setSidePane(find<SideContainerLogDetails>().root)
            setContent(viewContainerMain)
            direction = JFXDrawer.DrawerDirection.LEFT
            isOverLayVisible = true
            isResizableOnDrag = true
            defaultDrawerSize = 400.0
            setOnDrawerOpened {
                sidePaneHandler.notifyOnSidePaneChange()
            }
            setOnDrawerOpening {
                sidePaneHandler.notifyOnSidePaneChange()
            }
            setOnDrawerClosed {
                find<SideContainerLogDetails>().detach()
                sidePaneHandler.notifyOnSidePaneChange()
            }
            setOnDrawerClosing {
                sidePaneHandler.notifyOnSidePaneChange()
            }
        }
        viewSideDrawerTickets = jfxDrawer {
            style {
                padding = box(
                        left = 60.0.px,
                        top = 0.0.px,
                        right = 0.0.px,
                        bottom = 0.0.px
                )
            }
            setSidePane(find<SideContainerTickets>().root)
            setContent(viewSideDrawerLogDetails)
            direction = JFXDrawer.DrawerDirection.LEFT
            isOverLayVisible = true
            isResizableOnDrag = true
            defaultDrawerSize = 500.0
            setOnDrawerOpened {
                sidePaneHandler.notifyOnSidePaneChange()
            }
            setOnDrawerOpening {
                sidePaneHandler.notifyOnSidePaneChange()
            }
            setOnDrawerClosed {
                sidePaneHandler.notifyOnSidePaneChange()
                find<SideContainerTickets>().detach()
            }
            setOnDrawerClosing {
                sidePaneHandler.notifyOnSidePaneChange()
            }
        }
        viewContainerMenu.toFront()
    }

    override fun onDock() {
        super.onDock()
        // Init ui elements
        snackBar = JFXSnackbar(root as StackPane)
                .apply { toFront() }
        subsFocusChange = JavaFxObservable.valuesOf(primaryStage.focusedProperty())
                .subscribe({ focus ->
                    eventBus.post(EventFocusChange(focus))
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        presenter = MainPresenter()
        sidePaneHandler = SidePaneHandler(
                listener = object : SidePaneHandler.Listener {
                    override fun onSidePanelStateChange(state: SidePaneHandler.PaneState) {
                        when (state) {
                            SidePaneHandler.PaneState.CLOSED -> {
                                autoSyncWatcher.changeUpdateLock(
                                        isInLock = false,
                                        lockProcessName = "Drawer"
                                )
                            }
                            SidePaneHandler.PaneState.OPEN_ONLY_LOGS -> {
                                autoSyncWatcher.changeUpdateLock(
                                        isInLock = true,
                                        lockProcessName = "Drawer"
                                )
                                eventBus.post(EventFocusLogDetailsWidget())
                            }
                            SidePaneHandler.PaneState.OPEN_ALL -> {
                                autoSyncWatcher.changeUpdateLock(
                                        isInLock = true,
                                        lockProcessName = "Drawer"
                                )
                                eventBus.post(EventFocusTicketWidget())
                            }
                        }.javaClass
                    }
                },
                paneLogs = SidePaneStateProviderDrawer(viewSideDrawerLogDetails),
                paneTickets = SidePaneStateProviderDrawer(viewSideDrawerTickets)
        )
        primaryStage.setOnCloseRequest {
            if (logFreshnessChecker.isUpToDate()) {
                Platform.exit()
            } else {
                it.consume()
                find<LogFreshnessWidget>()
                        .openModal(
                                stageStyle = StageStyle.DECORATED,
                                modality = Modality.APPLICATION_MODAL,
                                block = false,
                                resizable = true
                        )
            }
        }

        // Init interactors
        syncInteractor.addLoadingListener(syncInteractorListener)
        eventBus.register(this)
        presenter.onAttach(this)
    }

    override fun onUndock() {
        presenter.onDetach()
        subsFocusChange?.unsubscribe()
        eventBus.unregister(this)
        syncInteractor.removeLoadingListener(syncInteractorListener)
    }

    //region Events

    @Subscribe
    fun onDisplayTypeChange(eventChangeDisplayType: EventChangeDisplayType) {
        jfxButtonDisplayView.graphic = when (eventChangeDisplayType.displayType) {
            DisplayType.TABLE_VIEW_DETAIL -> graphics.from(Glyph.DISPLAY_LIST, Color.WHITE, 20.0)
            DisplayType.CALENDAR_VIEW_DAY -> graphics.from(Glyph.DISPLAY_DAY, Color.WHITE, 20.0)
            DisplayType.CALENDAR_VIEW_WEEK -> graphics.from(Glyph.DISPLAY_WEEK, Color.WHITE, 20.0)
            DisplayType.GRAPHS -> null
        }
    }

    @Subscribe
    fun onOpenLogDetails(event: EventMainOpenLogDetails) {
        when (sidePaneHandler.sidePanelState()) {
            SidePaneHandler.PaneState.CLOSED -> {
                find<SideContainerLogDetails>().attach()
                viewSideDrawerLogDetails.open()
            }
            SidePaneHandler.PaneState.OPEN_ONLY_LOGS -> {}
            SidePaneHandler.PaneState.OPEN_ALL -> {}
        }.javaClass
    }

    @Subscribe
    fun onCloseLogDetails(event: EventMainCloseLogDetails) {
        when (sidePaneHandler.sidePanelState()) {
            SidePaneHandler.PaneState.CLOSED -> {}
            SidePaneHandler.PaneState.OPEN_ONLY_LOGS -> {
                viewSideDrawerLogDetails.close()
            }
            SidePaneHandler.PaneState.OPEN_ALL -> {
                viewSideDrawerLogDetails.close()
                viewSideDrawerTickets.close()
            }
        }.javaClass
    }

    @Subscribe
    fun onOpenTickets(event: EventMainOpenTickets) {
        when (sidePaneHandler.sidePanelState()) {
            SidePaneHandler.PaneState.CLOSED -> {}
            SidePaneHandler.PaneState.OPEN_ONLY_LOGS -> {
                find<SideContainerTickets>().attach()
                viewSideDrawerTickets.open()
            }
            SidePaneHandler.PaneState.OPEN_ALL -> { }
        }.javaClass
    }

    @Subscribe
    fun onCloseTickets(event: EventMainCloseTickets) {
        when (sidePaneHandler.sidePanelState()) {
            SidePaneHandler.PaneState.CLOSED -> {}
            SidePaneHandler.PaneState.OPEN_ONLY_LOGS -> { }
            SidePaneHandler.PaneState.OPEN_ALL -> {
                viewSideDrawerTickets.close()
            }
        }.javaClass
    }

    @Subscribe
    fun onAutoSync(event: EventAutoSync) {
        syncInteractor.syncActiveTime()
    }

    @Subscribe
    fun onFocusChange(event: EventFocusChange) {
        ticker.changeFocus(event.isInFocus)
        if (!event.isInFocus) {
            eventBus.post(EventEditMode(isInEdit = false))
        }
    }

    @Subscribe
    fun onSnackBarMessage(event: EventSnackBarMessage) {
        val label = Label(event.message)
                .apply {
                    addClass(Styles.emojiText)
                    style {
                        fontSize = 16.0.px
                    }
                    background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
                    val paddingHorizontal = 10.0
                    val paddingVertical = 8.0
                    padding = Insets(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal)
                    textFill = Color.WHITE
                    maxWidth = stageProperties.width
                }
        val hBox = HBox(10.0, label)
        snackBar.enqueue(JFXSnackbar.SnackbarEvent(hBox))
    }

    @Subscribe
    fun onNewVersion(event: EventNewVersion) {
        val viewMessage = hbox(spacing = 2) {
            background = Background(
                    BackgroundFill(
                            Color.BLACK,
                            CornerRadii(10.0, 10.0, 0.0, 0.0, false),
                            Insets.EMPTY
                    )
            )
            label("New version available!") {
                style {
                    padding = box(
                            vertical = 6.px,
                            horizontal = 10.px
                    )
                }
                textFill = Color.WHITE
                maxWidth = stageProperties.width
            }
            jfxButton("Info".toUpperCase()) {
                textFill = Color.WHITE
                action {
                    val widgetChangelog = find<ChangelogWidget>()
                    widgetChangelog.render(event.changelog)
                    widgetChangelog.openModal(
                            stageStyle = StageStyle.DECORATED,
                            modality = Modality.APPLICATION_MODAL,
                            block = false,
                            resizable = true
                    )
                }
            }
        }
        snackBar.enqueue(JFXSnackbar.SnackbarEvent(viewMessage, javafx.util.Duration(5000.0), null))
    }

    @Subscribe
    fun onEventEditLog(event: EventEditLog) {
        if (event.editType == LogEditType.NEW) {
            eventBus.post(EventMainOpenLogDetails())
            return
        }
        if (event.logs.isEmpty()) {
            logger.warn("No items are selected. Have you bound selected items ?")
            return
        }
        when (event.editType) {
            LogEditType.NEW -> {
                // No edit funct
            }
            LogEditType.UPDATE -> {
                resultDispatcher.publish(LogDetailsSideDrawerWidget.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventMainOpenLogDetails())
            }
            LogEditType.DELETE -> {
                warning(
                        header = "Warning",
                        content = "This will delete worklog. Are you sure you want to proceed?",
                        buttons = *arrayOf(ButtonType.NO, ButtonType.YES),
                        actionFn = { buttonType ->
                            when (buttonType) {
                                ButtonType.YES -> activeDisplayRepository.delete(event.logs.first())
                                else -> logger.info("Delete dialog dismissed")
                            }
                        }
                )

            }
            LogEditType.CLONE -> {
                val logToClone = event.logs.first()
                val newLog = Log.new(
                    timeProvider = timeProvider,
                    start = logToClone.time.startAsRaw,
                    end = logToClone.time.endAsRaw,
                    code = logToClone.code.code,
                    comment = logToClone.comment,
                    systemNote = "",
                    author = "",
                    remoteData = null
                )
                activeDisplayRepository.insertOrUpdate(newLog)
            }
            LogEditType.SPLIT -> {
                resultDispatcher.publish(TicketSplitWidget.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventInflateDialog(DialogType.TICKET_SPLIT))
            }
            LogEditType.WEBLINK -> {
                val activeLog = event.logs.first()
                val webLink = jiraLinkGenerator.webLinkFromInput(activeLog.code.code)
                if (webLink.isNotEmpty()) {
                    val message = EmojiParser.parseToUnicode("Copied $webLink :rocket:")
                    eventBus.post(EventSnackBarMessage(message))
                    hostServicesInteractor.ticketWebLinkToClipboard(webLink)
                } else {
                    val message = EmojiParser.parseToUnicode("Can't generate web link :boom:")
                    eventBus.post(EventSnackBarMessage(message))
                }
            }
            LogEditType.BROWSER -> {
                val activeLog = event.logs.first()
                val webLink = jiraLinkGenerator.webLinkFromInput(activeLog.code.code)
                if (webLink.isNotEmpty()) {
                    hostServicesInteractor.openLink(webLink)
                } else {
                    val message = EmojiParser.parseToUnicode("Can't open link :boom:")
                    eventBus.post(EventSnackBarMessage(message))
                }
            }
        }
    }

    //endregion

    @Subscribe
    fun eventInflateDialog(event: EventInflateDialog) {
        when (event.type) {
            DialogType.ACTIVE_CLOCK -> { logger.warn("LogDetailsWidget was moved to sidePanel (LogDetailsSideDrawerWidget)") }
            DialogType.LOG_EDIT -> { logger.warn("LogDetailsWidget was moved to sidePanel (LogDetailsSideDrawerWidget)") }
            DialogType.TICKET_SEARCH -> { logger.warn("TicketWidget was moved to sidePanel (TicketSideDrawerWidget)") }
            DialogType.TICKET_SPLIT -> {
                find<TicketSplitWidget>().openWindow(
                        stageStyle = StageStyle.DECORATED,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
            }
        }
    }

    fun showInfo(message: String) {
        information(
                header = "Info",
                content = message
        )
    }

    fun showError(message: String) {
        error(
                header = "Error",
                content = message
        )
    }

    fun showErrorAuth() {
        val buttonOpenSettings = ButtonType("Open 'Account settings'")
        alert(
                header = "Error",
                content = "Cannot connect to '${jiraClientProvider.hostname()}' with user '${jiraClientProvider.username()}'.\n" +
                        "Please check connection in 'Account settings'.\n" +
                        "Make sure you're able to connect by pressing 'TEST CONNECTION' to fix this issue",
                type = Alert.AlertType.ERROR,
                title = "Error",
                buttons = *arrayOf(buttonOpenSettings),
                actionFn = {
                    if (BuildConfig.oauth) {
                        find<AccountSettingsOauthWidget>().openModal(
                                stageStyle = StageStyle.DECORATED,
                                modality = Modality.APPLICATION_MODAL,
                                block = false,
                                resizable = true
                        )
                    } else {
                        find<AccountSettingsWidget>().openModal(
                                stageStyle = StageStyle.DECORATED,
                                modality = Modality.APPLICATION_MODAL,
                                block = false,
                                resizable = true
                        )
                    }
                }
        )
    }

    //region Listeners

    private val syncInteractorListener = object : IRemoteLoadListener {
        override fun onLoadChange(loading: Boolean) {}
        override fun onError(error: String?) {
            showError(error ?: "")
        }

        override fun onAuthError() {
            showErrorAuth()
        }
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(MainWidget::class.java)!!
    }

}