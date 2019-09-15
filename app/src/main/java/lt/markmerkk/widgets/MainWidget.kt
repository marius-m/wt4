package lt.markmerkk.widgets

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.*
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerPresenter
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidgetPresenterDefault
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.progress.ProgressWidget
import lt.markmerkk.ui_2.views.progress.ProgressWidgetPresenter
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.widgets.calendar.CalendarWidget
import lt.markmerkk.widgets.clock.ClockWidget
import lt.markmerkk.widgets.edit.LogDetailsWidget
import lt.markmerkk.widgets.settings.AccountSettingsWidget
import lt.markmerkk.widgets.tickets.PopUpChangeMainContent
import lt.markmerkk.widgets.tickets.PopUpSettings
import lt.markmerkk.widgets.tickets.TicketWidget
import org.slf4j.LoggerFactory
import rx.Subscription
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

class MainWidget : View(), ExternalSourceNode<StackPane> {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var strings: Strings
    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var eventBus: com.google.common.eventbus.EventBus
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logChangeValidator: LogChangeValidator
    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var connProvider: DBConnProvider
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var autoSyncWatcher: AutoSyncWatcher2

    lateinit var jfxButtonDisplayView: JFXButton
    lateinit var jfxButtonSettings: JFXButton
    lateinit var jfxContainerContentLeft: HBox
    lateinit var jfxContainerContentRight: HBox

    lateinit var snackBar: JFXSnackbar
    lateinit var widgetDateChange: QuickDateChangeWidget
    lateinit var widgetProgress: ProgressWidget
    lateinit var widgetLogQuickEdit: QuickEditContainerWidget

    private var subsFocusChange: Subscription? = null

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        setOnKeyReleased { keyEvent ->
            if (keyEvent.code == KeyCode.ALT) {
                find<CalendarWidget>().changeEditMode(false)
            }
        }
        setOnKeyPressed { keyEvent ->
            if (keyEvent.code == KeyCode.ALT) {
                find<CalendarWidget>().changeEditMode(true)
            }
            when {
                (keyEvent.code == KeyCode.S && keyEvent.isMetaDown)
                        || (keyEvent.code == KeyCode.S && keyEvent.isControlDown) -> {
                    resultDispatcher.publish(LogDetailsWidget.RESULT_DISPATCH_KEY_ACTIVE_CLOCK, true)
                    eventBus.post(EventInflateDialog(DialogType.ACTIVE_CLOCK))
                }
                (keyEvent.code == KeyCode.R && keyEvent.isMetaDown)
                        || (keyEvent.code == KeyCode.R && keyEvent.isControlDown) -> {
                    syncInteractor.syncLogs()
                    autoSyncWatcher.reset()
                }
            }
        }
        borderpane {
            left {
                vbox(spacing = 4) {
                    style {
                        backgroundColor.add(Styles.cBackgroundPrimary)
                        padding = box(vertical = 4.px, horizontal = 10.px)
                    }
                    add(find<ClockWidget>().root)
                    vbox(spacing = 4, alignment = Pos.BOTTOM_CENTER) {
                        vgrow = Priority.ALWAYS
                        jfxButtonDisplayView = jfxButton {
                            addClass(Styles.buttonMenu)
                            graphic = graphics.from(Glyph.VIEW, Color.WHITE, 20.0)
                            setOnAction {
                                PopUpChangeMainContent(graphics, eventBus, jfxButtonDisplayView)
                                        .show()
                            }
                        }
                        jfxButtonSettings = jfxButton {
                            addClass(Styles.buttonMenu)
                            graphic = graphics.from(Glyph.SETTINGS, Color.WHITE, 20.0)
                            setOnAction {
                                PopUpSettings(graphics, jfxButtonSettings)
                                        .show()
                            }
                        }
                    }
                }
            }
            center {
                borderpane {
                    top {
                        borderpane {
                            left {
                                jfxContainerContentLeft = hbox { }
                            }
                            right {
                                jfxContainerContentRight = hbox { }
                            }
                        }
                    }
                    center {
                        add(find<CalendarWidget>())
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        val titleSuffix = if (BuildConfig.debug) "(DEBUG)" else ""
        title = "WT4 - ${BuildConfig.VERSION} $titleSuffix"
        // Init ui elements
        widgetDateChange = QuickDateChangeWidget(
                graphics,
                QuickDateChangeWidgetPresenterDefault(
                        this,
                        logStorage
                )
        )
        widgetProgress = ProgressWidget(
                presenter = ProgressWidgetPresenter(
                        syncInteractor = syncInteractor,
                        autoSyncWatcher = autoSyncWatcher
                ),
                graphics = graphics
        )
        widgetLogQuickEdit = QuickEditContainerWidget(
                presenter = QuickEditContainerPresenter(eventBus),
                logStorage = logStorage,
                timeProvider = timeProvider,
                graphics = graphics,
                logChangeValidator = logChangeValidator
        )
        snackBar = JFXSnackbar(root as StackPane)
        jfxContainerContentRight.children.add(widgetProgress.root)
        jfxContainerContentLeft.children.add(widgetDateChange.root)
        jfxContainerContentLeft.children.add(widgetLogQuickEdit.root)
        subsFocusChange = JavaFxObservable.valuesOf(primaryStage.focusedProperty())
                .subscribe { eventBus.post(EventFocusChange(it)) }

        // Init interactors
        syncInteractor.addLoadingListener(syncInteractorListener)
        eventBus.register(this)
        widgetDateChange.onAttach()
        widgetProgress.onAttach()
        widgetLogQuickEdit.onAttach()
    }

    override fun onUndock() {
        subsFocusChange?.unsubscribe()
        widgetLogQuickEdit.onDetach()
        widgetProgress.onDetach()
        widgetDateChange.onDetach()
        eventBus.unregister(this)
        syncInteractor.removeLoadingListener(syncInteractorListener)
        if (hourGlass.state == HourGlass.State.RUNNING) {
            hourGlass.stop()
        }
    }

    //region Events

    @Subscribe
    fun onAutoSync(event: EventAutoSync) {
        syncInteractor.syncLogs()
    }

    @Subscribe
    fun onFocusChange(event: EventFocusChange) {
        if (!event.isInFocus) {
            find<CalendarWidget>().changeEditMode(false)
        }
    }

    @Subscribe
    fun onSnackBarMessage(event: EventSnackBarMessage) {
        val label = Label(event.message)
                .apply {
                    val paddingHorizontal = 10.0
                    val paddingVertical = 8.0
                    padding = Insets(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal)
                    textFill = Paint.valueOf("white")
                    maxWidth = stageProperties.width
                }
        val hBox = HBox(10.0, label)
        snackBar.enqueue(JFXSnackbar.SnackbarEvent(hBox))
    }

    @Subscribe
    fun onEventEditLog(event: EventEditLog) {
        if (event.logs.isEmpty()) {
            logger.warn("No items are selected. Have you bound selected items ?")
            return
        }
        when (event.editType) {
            LogEditType.UPDATE -> {
                resultDispatcher.publish(LogDetailsWidget.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventInflateDialog(DialogType.LOG_EDIT))
            }
            LogEditType.DELETE -> logStorage.delete(event.logs.first())
            LogEditType.CLONE -> {
                val logToClone = event.logs.first()
                val newLog = SimpleLogBuilder()
                        .setStart(logToClone.start)
                        .setEnd(logToClone.end)
                        .setTask(logToClone.task)
                        .setComment(logToClone.comment)
                        .build()
                logStorage.insert(newLog)
            }
            LogEditType.SPLIT -> {
                resultDispatcher.publish(TicketSplitWidget.RESULT_DISPATCH_KEY_ENTITY, event.logs.first())
                eventBus.post(EventInflateDialog(DialogType.TICKET_SPLIT))
            }
        }.javaClass
    }

    //endregion

    override fun rootNode(): StackPane = root as StackPane

    @Subscribe
    fun eventInflateDialog(event: EventInflateDialog) {
        when (event.type) {
            DialogType.ACTIVE_CLOCK -> {
                find<LogDetailsWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
            }
            DialogType.LOG_EDIT -> {
                find<LogDetailsWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
            }
            DialogType.TICKET_SEARCH -> {
                find<TicketWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
                        modality = Modality.APPLICATION_MODAL,
                        block = false,
                        resizable = true
                )
                find<TicketWidget>().primaryStage.toFront()
            }
            DialogType.TICKET_SPLIT -> {
                find<TicketSplitWidget>().openModal(
                        stageStyle = StageStyle.UTILITY,
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
                content = "Cannot connect to '${userSettings.host}' with user '${userSettings.username}'.\n" +
                        "Please check connection in 'Account settings'.\n" +
                        "Make sure you're able to connect by pressing 'TEST CONNECTION' to fix this issue",
                type = Alert.AlertType.ERROR,
                title = "Error",
                buttons = *arrayOf(buttonOpenSettings),
                actionFn = {
                    find<AccountSettingsWidget>().openModal(
                            stageStyle = StageStyle.UTILITY,
                            modality = Modality.APPLICATION_MODAL,
                            block = false,
                            resizable = true
                    )
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