package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.utils.LogUtils
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class StatisticsController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonDismiss: JFXButton
    @FXML lateinit var jfxLabel: Label

    @Inject lateinit var logStorage: LogStorage

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component().presenterComponent().inject(this)

        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        jfxLabel.text = LogUtils.formatShortDuration(logStorage.total().toLong())
    }

    @PreDestroy
    fun destroy() {
    }

}