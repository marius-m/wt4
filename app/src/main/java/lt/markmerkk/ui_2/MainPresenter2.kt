package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.*
import lt.markmerkk.*
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.EventChangeDisplayType
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.interactors.ClockRunBridge
import lt.markmerkk.interactors.ClockRunBridgeImpl
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.day.DayView
import lt.markmerkk.ui.display.DisplayLogView
import lt.markmerkk.ui.graphs.GraphsFxView
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.ui.week.WeekView
import lt.markmerkk.ui_2.bridges.*
import lt.markmerkk.utils.hourglass.HourGlass
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class MainPresenter2 : Initializable, ExternalSourceNode<StackPane> {

    @FXML lateinit var jfxRoot: BorderPane
    @FXML lateinit var jfxButtonCommit: JFXButton
    @FXML lateinit var jfxTextFieldCommit: JFXTextField
    @FXML lateinit var jfxTextFieldTicket: JFXTextField
    @FXML lateinit var jfxButtonClock: JFXButton
    @FXML lateinit var jfxButtonClockSettings: JFXButton
    @FXML lateinit var jfxToggleClock: JFXToggleNode
    @FXML lateinit var jfxContainerCommit: Region
    @FXML lateinit var jfxButtonSettings: JFXButton
    @FXML lateinit var jfxButtonDate: JFXButton
    @FXML lateinit var jfxButtonDisplayView: JFXButton
    @FXML lateinit var jfxProgressBar: JFXProgressBar

    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieButtonDisplayView: UIEButtonDisplayView
    lateinit var uieButtonDate: UIEButtonDate
    lateinit var uieButtonSettings: UIEButtonSettings
    lateinit var uieCommitContainer: UIECommitContainer
    lateinit var uieCenterView: UIECenterView
    lateinit var uieProgressView: UIEProgressView
    lateinit var clockRunBridge: ClockRunBridge
    lateinit var snackBar: JFXSnackbar

    var currentDisplayType = DisplayType.CALENDAR_VIEW_DAY

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)

        // Init ui elements
        snackBar = JFXSnackbar(jfxRoot)
        uieButtonDate = UIEButtonDate(graphics, this, jfxButtonDate)
        uieButtonSettings = UIEButtonSettings(graphics, this, jfxButtonSettings, syncInteractor)
        uieButtonDisplayView = UIEButtonDisplayView(graphics, this, jfxButtonDisplayView, buttonChangeDisplayViewExternalListener)
        uieButtonClock = UIEButtonClock(
                graphics,
                this,
                buttonClockListener,
                jfxButtonClock,
                jfxButtonClockSettings,
                jfxToggleClock
        )
        uieCommitContainer = UIECommitContainer(
                graphics,
                containerCommitListener,
                jfxButtonCommit,
                jfxTextFieldTicket,
                jfxTextFieldCommit,
                jfxContainerCommit
        )
        uieCenterView = UIECenterView(jfxRoot)
        uieProgressView = UIEProgressView(jfxRoot, jfxProgressBar)
        changeDisplayByDisplayType(currentDisplayType)

        // Init interactors
        clockRunBridge = ClockRunBridgeImpl(
                uieCommitContainer,
                uieButtonClock,
                hourGlass,
                logStorage
        )
        eventBus.register(this)
        syncInteractor.addLoadingListener(uieProgressView)
    }

    @PreDestroy
    fun destroy() {
        syncInteractor.removeLoadingListener(uieProgressView)
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
        snackBar.show(event.message, Const.TIMEOUT_2s)
    }

    //endregion

    //region Convenience

    fun changeDisplayByDisplayType(displayType: DisplayType) {
        val oldView = uieCenterView.raw()
        when (displayType) {
            DisplayType.TABLE_VIEW_SIMPLE -> {
                logStorage.displayType = DisplayTypeLength.DAY
                uieCenterView.populate(
                        DisplayLogView(
                                listener = simpleUpdateListener,
                                isViewSimplified = true
                        )
                )
            }
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
                uieCenterView.populate(WeekView(simpleUpdateListener))
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
    val simpleUpdateListener: UpdateListener = object : UpdateListener {

        override fun onUpdate(entity: SimpleLog) {
            val logEditDialog = LogEditDialog(entity)
            val jfxDialog = logEditDialog.view as JFXDialog
            jfxDialog.show(rootNode())
            jfxDialog.setOnDialogClosed { InjectorNoDI.forget(logEditDialog) }
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

    val buttonChangeDisplayViewExternalListener: UIEButtonDisplayView.ExternalListener = object : UIEButtonDisplayView.ExternalListener {
        override fun currentDisplayType(): DisplayType {
            return currentDisplayType
        }
    }

    private val containerCommitListener: UIECommitContainer.Listener = object : UIECommitContainer.Listener {
        override fun onClickSend(ticket: String, message: String) {
            clockRunBridge.log(ticket, message)
        }
    }

    private val buttonClockListener: UIEButtonClock.Listener = object : UIEButtonClock.Listener {

        override fun onClickClock(isSelected: Boolean) {
            clockRunBridge.setRunning(isSelected)
        }

        override fun onClickClockSettings() {}

    }

    //endregion

}