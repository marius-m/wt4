package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import lt.markmerkk.Main
import lt.markmerkk.Main2
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
    @FXML lateinit var jfxButtonClock: JFXButton
    @FXML lateinit var jfxButtonClockSettings: JFXButton
    @FXML lateinit var jfxToggleClock: JFXToggleNode
    @FXML lateinit var jfxContainerCommit: Region
    @FXML lateinit var jfxListViewOutput: JFXTreeTableView<UIEListView.TreeLog>
    @FXML lateinit var jfxColumnFirst: JFXTreeTableColumn<UIEListView.TreeLog, String>

    @Inject lateinit var hourGlass: HourGlass

    lateinit var uieButtonCommit: UIEButtonCommit
    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieCommitContainer: UIECommitContainer
    lateinit var uieListView: UIEListView
    lateinit var clockRunInteractor: ClockRunBridge

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
        uieButtonCommit = UIEButtonCommit(jfxButtonCommit)
        uieCommitContainer = UIECommitContainer(jfxContainerCommit)
        uieListView = UIEListView(jfxListViewOutput, jfxColumnFirst)

        // Init interactors
        clockRunInteractor = ClockRunBridgeImpl(
                uieCommitContainer,
                uieButtonClock,
                hourGlass
        )
    }

    @PreDestroy
    fun destroy() {
        if (hourGlass.state == HourGlass.State.RUNNING) {
            hourGlass.stop()
        }
    }

    override fun rootNode(): StackPane = jfxRoot.parent as StackPane

    //region Listeners

    private val buttonClockListener: UIEButtonClock.Listener = object : UIEButtonClock.Listener {

        override fun onClickClock(isSelected: Boolean) {
            clockRunInteractor.setRunning(isSelected)
        }

        override fun onClickClockSettings() {}

    }

    //endregion

}