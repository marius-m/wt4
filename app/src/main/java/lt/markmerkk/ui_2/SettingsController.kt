package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXProgressBar
import com.jfoenix.controls.JFXSpinner
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.text.Text
import lt.markmerkk.Main
import lt.markmerkk.Strings
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * @author mariusmerkevicius
 * @since 2017-09-12
 */
class SettingsController : Initializable {

    @FXML lateinit var jfxInfo: Text
    @FXML lateinit var jfxSpinner: JFXSpinner

    @Inject lateinit var strings: Strings

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)

        jfxInfo.text = strings.getString("settings_info")
    }

    @PreDestroy
    fun destroy() {

    }
}