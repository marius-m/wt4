package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import com.jfoenix.svg.SVGGlyph
import javafx.animation.Interpolator
import javafx.animation.TranslateTransition
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
import javafx.scene.paint.Color
import javafx.util.Callback
import javafx.util.Duration
import java.net.URL
import java.util.*


class MainPresenter2 : Initializable {

    @FXML lateinit var button: JFXButton
    @FXML lateinit var clockButton: JFXButton
    @FXML lateinit var clockToggle: JFXToggleNode
    @FXML lateinit var inputContainer: Region
    @FXML lateinit var mainContainer: BorderPane
    @FXML lateinit var outputListView: JFXTreeTableView<TreeLog>
    @FXML lateinit var firstNameColumn: JFXTreeTableColumn<TreeLog, String>
    @FXML lateinit var lastNameColumn: JFXTreeTableColumn<TreeLog, String>
    @FXML lateinit var ageColumn: JFXTreeTableColumn<TreeLog, Int>

    val logs: ObservableList<TreeLog> = FXCollections.observableArrayList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val sendGlyph = sendGlyph()
        sendGlyph.setSize(20.0, 20.0)
        button.graphic = sendGlyph
        clockToggle.setOnAction {
            if (clockToggle.isSelected) {
                showInput2()
            } else {
                hideInput2()
            }
        }
        val clockGlyph = clockGlyph()
        clockGlyph.setSize(20.0, 20.0)
        clockButton.graphic = clockGlyph
        clockButton.text = ""

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
        outputListView.root = RecursiveTreeItem<TreeLog>(
                logs,
                RecursiveTreeObject<TreeLog>::getChildren
        )
        outputListView.isShowRoot = false
        for (i in 0..200) {
            logs.add(TreeLog(
                    SimpleStringProperty("name" + i),
                    SimpleStringProperty("surname" + i),
                    SimpleIntegerProperty(i)
            ))
        }

    }

    fun hideInput2() {
        val translateTransition = TranslateTransition(Duration.millis(100.0), inputContainer)
        translateTransition.fromY = inputContainer.translateY
        translateTransition.toY = inputContainer.height + 20
        translateTransition.interpolator = Interpolator.EASE_IN
        translateTransition.setOnFinished {
            inputContainer.isManaged = false
            inputContainer.isVisible = false
        }
        translateTransition.play()

        val clockGlyph = clockGlyph()
        clockGlyph.setSize(25.0, 25.0)
        clockButton.graphic = clockGlyph
        clockButton.text = ""
    }

    fun showInput2() {
        inputContainer.isManaged = true
        inputContainer.isVisible = true
        val translateTransition = TranslateTransition(Duration.millis(100.0), inputContainer)
        translateTransition.fromY = inputContainer.translateY
        translateTransition.toY = 0.0
        translateTransition.interpolator = Interpolator.EASE_IN
        translateTransition.play()

        clockButton.graphic = null
        clockButton.text = "1h 30m"
    }

    fun sendGlyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "test",
                "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143 11.429 0 20.571 6.286z",
                Color.WHITE
        )
    }

    fun clockGlyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "clock-o",
                "M512 640v-256q0-8-5.143-13.143t-13.143-5.143h-182.857q-8 0-13.143 5.143t-5.143 13.143v36.571q0 8 5.143 13.143t13.143 5.143h128v201.143q0 8 5.143 13.143t13.143 5.143h36.571q8 0 13.143-5.143t5.143-13.143zM749.714 438.857q0 84.571-41.714 156t-113.143 113.143-156 41.714-156-41.714-113.143-113.143-41.714-156 41.714-156 113.143-113.143 156-41.714 156 41.714 113.143 113.143 41.714 156zM877.714 438.857q0-119.429-58.857-220.286t-159.714-159.714-220.286-58.857-220.286 58.857-159.714 159.714-58.857 220.286 58.857 220.286 159.714 159.714 220.286 58.857 220.286-58.857 159.714-159.714 58.857-220.286z",
                Color.WHITE
        );
    }

}

class TreeLog(
        val firstName: StringProperty = SimpleStringProperty(),
        val lastName: StringProperty = SimpleStringProperty(),
        val age: IntegerProperty = SimpleIntegerProperty()
) : RecursiveTreeObject<TreeLog>()
