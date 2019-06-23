package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXToggleNode
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Paint
import lt.markmerkk.*
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventChangeDisplayType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.interactors.ClockRunBridge
import lt.markmerkk.interactors.ClockRunBridgeImpl
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.day.DayView
import lt.markmerkk.ui.display.DisplayLogView
import lt.markmerkk.ui.graphs.GraphsFxView
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.ui.week2.WeekView2
import lt.markmerkk.ui_2.bridges.UIEButtonClock
import lt.markmerkk.ui_2.bridges.UIEButtonDisplayView
import lt.markmerkk.ui_2.bridges.UIEButtonSettings
import lt.markmerkk.ui_2.bridges.UIECenterView
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerPresenter
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidget
import lt.markmerkk.ui_2.views.date.QuickDateChangeWidgetPresenterDefault
import lt.markmerkk.ui_2.views.progress.ProgressWidget
import lt.markmerkk.ui_2.views.progress.ProgressWidgetPresenter
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.validators.LogChangeValidator
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class MainPresenter2 : Initializable, ExternalSourceNode<StackPane> {

    @FXML lateinit var jfxRoot: BorderPane
    @FXML lateinit var jfxButtonClock: JFXButton
    @FXML lateinit var jfxButtonClockSettings: JFXButton
    @FXML lateinit var jfxToggleClock: JFXToggleNode
    @FXML lateinit var jfxButtonSettings: JFXButton
    @FXML lateinit var jfxButtonDisplayView: JFXButton
    @FXML lateinit var jfxContainerContent: BorderPane
    @FXML lateinit var jfxContainerContentLeft: HBox
    @FXML lateinit var jfxContainerContentRight: HBox

    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var strings: Strings
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logChangeValidator: LogChangeValidator

    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieButtonDisplayView: UIEButtonDisplayView
    lateinit var uieButtonSettings: UIEButtonSettings
    lateinit var uieCenterView: UIECenterView
    lateinit var clockRunBridge: ClockRunBridge
    lateinit var snackBar: JFXSnackbar
    lateinit var dialogInflater: DialogInflater
    lateinit var widgetDateChange: QuickDateChangeWidget
    lateinit var widgetProgress: ProgressWidget
    lateinit var widgetLogQuickEdit: QuickEditContainerWidget

    var currentDisplayType = DisplayType.CALENDAR_VIEW_DAY

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        // Init ui elements
        widgetDateChange = QuickDateChangeWidget(
                graphics,
                QuickDateChangeWidgetPresenterDefault(
                        this,
                        logStorage
                )
        )
        widgetProgress = ProgressWidget(
                presenter = ProgressWidgetPresenter(syncInteractor),
                graphics = graphics
        )
        widgetLogQuickEdit = QuickEditContainerWidget(
                presenter = QuickEditContainerPresenter(eventBus),
                logStorage = logStorage,
                timeProvider = timeProvider,
                graphics = graphics,
                logChangeValidator = logChangeValidator
        )
        snackBar = JFXSnackbar(jfxRoot)
        uieButtonSettings = UIEButtonSettings(graphics, strings, this, jfxButtonSettings, eventBus)
        uieButtonDisplayView = UIEButtonDisplayView(graphics, this, jfxButtonDisplayView, buttonChangeDisplayViewExternalListener)
        uieButtonClock = UIEButtonClock(
                graphics,
                this,
                buttonClockListener,
                jfxButtonClock,
                jfxButtonClockSettings,
                jfxToggleClock
        )
        uieCenterView = UIECenterView(jfxContainerContent)
        jfxContainerContentRight.children.add(widgetProgress.root)
        jfxContainerContentLeft.children.add(widgetDateChange.root)
        jfxContainerContentLeft.children.add(widgetLogQuickEdit.root)
        changeDisplayByDisplayType(currentDisplayType)

        // Init interactors
        clockRunBridge = ClockRunBridgeImpl(
                uieButtonClock,
                hourGlass,
                logStorage
        )
        dialogInflater = DialogInflater(this, eventBus)
        eventBus.register(this)
        dialogInflater.onAttach()
        widgetDateChange.onAttach()
        widgetProgress.onAttach()
        widgetLogQuickEdit.onAttach()
    }

    @PreDestroy
    fun destroy() {
        widgetLogQuickEdit.onDetach()
        widgetProgress.onDetach()
        widgetDateChange.onDetach()
        dialogInflater.onDetach()
        eventBus.unregister(this)
        if (hourGlass.state == HourGlass.State.RUNNING) {
            hourGlass.stop()
        }
    }

    //region Events

    // todo : function will probably will have to be extracted to somewhere else
    @Subscribe
    fun onDisplayTypeChange(eventChangeDisplayType: EventChangeDisplayType) {
        changeDisplayByDisplayType(eventChangeDisplayType.displayType)
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

    //endregion

    //region Convenience

    fun changeDisplayByDisplayType(displayType: DisplayType) {
        val oldView = uieCenterView.raw()
        when (displayType) {
            DisplayType.TABLE_VIEW_DETAIL -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(
                        DisplayLogView(
                                listener = simpleUpdateListener,
                                isViewSimplified = false
                        )
                )
            }
            DisplayType.CALENDAR_VIEW_DAY -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(DayView(simpleUpdateListener))
            }
            DisplayType.CALENDAR_VIEW_WEEK -> {
                logStorage.displayType = DisplayTypeLength.WEEK
                uieCenterView.populate(WeekView2(simpleUpdateListener))
            }
            DisplayType.GRAPHS -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(GraphsFxView())
            }
            else -> throw IllegalStateException("Display cannot be handled")
        }
        InjectorNoDI.forget(oldView)
        currentDisplayType = displayType
    }

    //endregion

    override fun rootNode(): StackPane = jfxRoot.parent as StackPane

    //region Listeners

    // todo : move this later on
    private val simpleUpdateListener = object : UpdateListener {

        override fun onUpdate(entity: SimpleLog) {
            resultDispatcher.publish(LogEditController.RESULT_DISPATCH_KEY_ENTITY, entity)
            dialogInflater.eventInflateDialog(EventInflateDialog(DialogType.LOG_EDIT))
        }

        override fun onDelete(entity: SimpleLog) {
            logStorage.delete(entity)
        }

        override fun onClone(entity: SimpleLog) {
            val newLog = SimpleLogBuilder()
                    .setStart(entity.start)
                    .setEnd(entity.end)
                    .setTask(entity.task)
                    .setComment(entity.comment)
                    .build()
            logStorage.insert(newLog)
        }

    }

    private val buttonChangeDisplayViewExternalListener = object : UIEButtonDisplayView.ExternalListener {
        override fun currentDisplayType(): DisplayType {
            return currentDisplayType
        }
    }

    private val buttonClockListener = object : UIEButtonClock.Listener {

        override fun onClickClock(isSelected: Boolean) {
            clockRunBridge.setRunning(isSelected)
        }

        override fun onClickClockSettings() {
            eventBus.post(EventInflateDialog(DialogType.ACTIVE_CLOCK))
        }

    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.MAIN)
    }

}