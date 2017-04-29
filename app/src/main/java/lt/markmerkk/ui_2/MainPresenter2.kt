package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TreeTableColumn
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import lt.markmerkk.ui_2.bridges.UIEButtonClock
import lt.markmerkk.ui_2.bridges.UIEButtonCommit
import lt.markmerkk.ui_2.bridges.UIECommitContainer
import lt.markmerkk.ui_2.bridges.UIEListView
import lt.markmerkk.ui_2.interactors.ClockRunInteractor
import lt.markmerkk.ui_2.interactors.ClockRunInteractorImpl
import java.net.URL
import java.util.*

class MainPresenter2 : Initializable {

    @FXML lateinit var jfxCommitButton: JFXButton
    @FXML lateinit var jfxClockButton: JFXButton
    @FXML lateinit var jfxClockToggle: JFXToggleNode
    @FXML lateinit var jfxCommitContainer: Region
    @FXML lateinit var jfxMainContainer: BorderPane
    @FXML lateinit var jfxOutputListView: JFXTreeTableView<UIEListView.TreeLog>
    @FXML lateinit var jfxFirstColumn: JFXTreeTableColumn<UIEListView.TreeLog, String>

    lateinit var uieButtonCommit: UIEButtonCommit
    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieCommitContainer: UIECommitContainer
    lateinit var uieListView: UIEListView
    lateinit var clockRunInteractor: ClockRunInteractor

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        uieButtonClock = UIEButtonClock(jfxClockButton)
        uieButtonCommit = UIEButtonCommit(jfxCommitButton)
        uieCommitContainer = UIECommitContainer(jfxCommitContainer)
        uieListView = UIEListView(jfxOutputListView, jfxFirstColumn)
        clockRunInteractor = ClockRunInteractorImpl(uieCommitContainer, uieButtonClock)

        jfxClockToggle.setOnAction {
            if (jfxClockToggle.isSelected) {
                clockRunInteractor.setRunning(true)
            } else {
                clockRunInteractor.setRunning(false)
            }
        }
    }

}