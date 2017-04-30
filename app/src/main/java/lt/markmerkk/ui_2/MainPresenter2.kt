package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import lt.markmerkk.ui_2.bridges.UIEButtonClock
import lt.markmerkk.ui_2.bridges.UIEButtonCommit
import lt.markmerkk.ui_2.bridges.UIECommitContainer
import lt.markmerkk.ui_2.bridges.UIEListView
import lt.markmerkk.ui_2.interactors.ClockRunInteractor
import lt.markmerkk.ui_2.interactors.ClockRunInteractorImpl
import java.net.URL
import java.util.*

class MainPresenter2 : Initializable {

    @FXML lateinit var jfxButtonCommit: JFXButton
    @FXML lateinit var jfxButtonClock: JFXButton
    @FXML lateinit var jfxButtonClockSettings: JFXButton
    @FXML lateinit var jfxToggleClock: JFXToggleNode
    @FXML lateinit var jfxContainerCommit: Region
    @FXML lateinit var jfxContainerMain: StackPane
    @FXML lateinit var jfxListViewOutput: JFXTreeTableView<UIEListView.TreeLog>
    @FXML lateinit var jfxColumnFirst: JFXTreeTableColumn<UIEListView.TreeLog, String>

    lateinit var uieButtonCommit: UIEButtonCommit
    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieCommitContainer: UIECommitContainer
    lateinit var uieListView: UIEListView
    lateinit var clockRunInteractor: ClockRunInteractor

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        uieButtonClock = UIEButtonClock(jfxButtonClock, jfxButtonClockSettings)
        uieButtonCommit = UIEButtonCommit(jfxButtonCommit)
        uieCommitContainer = UIECommitContainer(jfxContainerCommit)
        uieListView = UIEListView(jfxListViewOutput, jfxColumnFirst)
        clockRunInteractor = ClockRunInteractorImpl(uieCommitContainer, uieButtonClock)

        jfxToggleClock.setOnAction {
            if (jfxToggleClock.isSelected) {
                clockRunInteractor.setRunning(true)
            } else {
                clockRunInteractor.setRunning(false)
            }
        }
        jfxButtonClockSettings.setOnAction {
            val clockEditDialog = ClockEditDialog()
            val jfxDialog = clockEditDialog.view as JFXDialog
            jfxDialog.show(jfxContainerMain)
        }
    }

}