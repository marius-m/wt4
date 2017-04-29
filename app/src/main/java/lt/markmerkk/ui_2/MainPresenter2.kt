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
    @FXML lateinit var jfxOutputListView: JFXTreeTableView<TreeLog>
    @FXML lateinit var firstNameColumn: JFXTreeTableColumn<TreeLog, String>
    @FXML lateinit var lastNameColumn: JFXTreeTableColumn<TreeLog, String>
    @FXML lateinit var ageColumn: JFXTreeTableColumn<TreeLog, Int>

    val logs: ObservableList<TreeLog> = FXCollections.observableArrayList()

    lateinit var uieButtonCommit: UIEButtonCommit
    lateinit var uieButtonClock: UIEButtonClock
    lateinit var uieCommitContainer: UIECommitContainer
    lateinit var clockRunInteractor: ClockRunInteractor

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        uieButtonClock = UIEButtonClock(jfxClockButton)
        uieButtonCommit = UIEButtonCommit(jfxCommitButton)
        uieCommitContainer = UIECommitContainer(jfxCommitContainer)
        clockRunInteractor = ClockRunInteractorImpl(uieCommitContainer, uieButtonClock)

        jfxClockToggle.setOnAction {
            if (jfxClockToggle.isSelected) {
                clockRunInteractor.setRunning(true)
            } else {
                clockRunInteractor.setRunning(false)
            }
        }

        firstNameColumn.setCellValueFactory({ param: TreeTableColumn.CellDataFeatures<TreeLog, String> ->
            if (firstNameColumn.validateValue(param)) {
                param.value.value.firstName
            } else {
                firstNameColumn.getComputedValue(param)
            }
        })
//        lastNameColumn.setCellValueFactory({ param: TreeTableColumn.CellDataFeatures<TreeLog, String> ->
//            param.value.value.lastName
//        })
//        ageColumn.setCellValueFactory({ param: TreeTableColumn.CellDataFeatures<TreeLog, Int> ->
//            param.value.value.age.asObject()
//        })
        jfxOutputListView.root = RecursiveTreeItem<TreeLog>(
                logs,
                RecursiveTreeObject<TreeLog>::getChildren
        )
        jfxOutputListView.isShowRoot = false
        for (i in 0..200) {
            logs.add(TreeLog(
                    SimpleStringProperty("name" + i),
                    SimpleStringProperty("surname" + i),
                    SimpleIntegerProperty(i)
            ))
        }
    }

}

class TreeLog(
        val firstName: StringProperty = SimpleStringProperty(),
        val lastName: StringProperty = SimpleStringProperty(),
        val age: IntegerProperty = SimpleIntegerProperty()
) : RecursiveTreeObject<TreeLog>()
