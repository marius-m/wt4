package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.fxml.FXML
import javafx.fxml.Initializable
import java.net.URL
import java.util.*

class DisplaySelectDialogController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonAccept: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton

    @FXML lateinit var jfxDate: JFXDatePicker

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        jfxButtonCancel.setOnAction {
            jfxDialog.close()
        }
    }

}