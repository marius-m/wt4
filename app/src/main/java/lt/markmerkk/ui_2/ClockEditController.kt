package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import java.net.URL
import java.util.*

class ClockEditController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonAccept: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton
    @FXML lateinit var jfxContentView: BorderPane
    @FXML lateinit var jfxHeaderLabel: Label

    @FXML lateinit var jfxDateOverlayFrom: JFXDatePicker
    @FXML lateinit var jfxTimeOverlayFrom: JFXDatePicker
    @FXML lateinit var jfxDateOverlayTo: JFXDatePicker
    @FXML lateinit var jfxTimeOverlayTo: JFXDatePicker

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        jfxButtonCancel.setOnAction {
            jfxDialog.close()
        }

        println("Showing time: " + jfxDateOverlayFrom.isShowTime)

    }
}