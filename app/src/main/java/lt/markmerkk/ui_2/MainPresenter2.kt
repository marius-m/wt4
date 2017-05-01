package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import lt.markmerkk.IDataStorage
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.Main2
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui_2.bridges.*
import lt.markmerkk.interactors.ClockRunBridge
import lt.markmerkk.interactors.ClockRunBridgeImpl
import lt.markmerkk.utils.IssueSplitImpl
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class MainPresenter2 : Initializable, ExternalSourceNode<StackPane> {

    @FXML lateinit var jfxRoot: BorderPane
    @FXML lateinit var jfxButtonCommit: JFXButton
    @FXML lateinit var jfxTextFieldCommit: JFXTextField
    @FXML lateinit var jfxButtonClock: JFXButton
    @FXML lateinit var jfxButtonClockSettings: JFXButton
    @FXML lateinit var jfxToggleClock: JFXToggleNode
    @FXML lateinit var jfxContainerCommit: Region
    @FXML lateinit var jfxListViewOutput: JFXTreeTableView<UIEListView.TreeLog>
    @FXML lateinit var jfxColumnTicket: JFXTreeTableColumn<UIEListView.TreeLog, String>
    @FXML lateinit var jfxColumnDuration: JFXTreeTableColumn<UIEListView.TreeLog, String>
    @FXML lateinit var jfxColumnMessage: JFXTreeTableColumn<UIEListView.TreeLog, String>

    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var logStorage: LogStorage

    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieCommitContainer: UIECommitContainer
    lateinit var uieListView: UIEListView
    lateinit var clockRunBridge: ClockRunBridge

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main2.Companion.component!!.presenterComponent().inject(this)

        // Init ui elements
        uieButtonClock = UIEButtonClock(
                this,
                buttonClockListener,
                jfxButtonClock,
                jfxButtonClockSettings,
                jfxToggleClock
        )
        uieCommitContainer = UIECommitContainer(
                containerCommitListener,
                jfxButtonCommit,
                jfxTextFieldCommit,
                jfxContainerCommit
        )
        uieListView = UIEListView(
                logStorage,
                jfxListViewOutput,
                jfxColumnTicket,
                jfxColumnDuration,
                jfxColumnMessage
        )

        // Init interactors
        clockRunBridge = ClockRunBridgeImpl(
                uieCommitContainer,
                uieButtonClock,
                hourGlass,
                logStorage
        )
        logStorage.register(uieListView)
    }

    @PreDestroy
    fun destroy() {
        logStorage.unregister(uieListView)
        if (hourGlass.state == HourGlass.State.RUNNING) {
            hourGlass.stop()
        }
    }

    override fun rootNode(): StackPane = jfxRoot.parent as StackPane

    //region Listeners

    private val containerCommitListener: UIECommitContainer.Listener = object : UIECommitContainer.Listener {
        override fun onClickSend(message: String) {
            clockRunBridge.log(message)
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