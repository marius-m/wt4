package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.Strings
import lt.markmerkk.utils.ConfigSetSettings
import lt.markmerkk.utils.ConfigSetSettingsImpl
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class ProfilesController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonDismiss: JFXButton
    @FXML lateinit var jfxButtonApply: JFXButton
    @FXML lateinit var jfxProfileSelection: JFXComboBox<String>
    @FXML lateinit var jfxInfo: Label

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var configSetSettings: ConfigSetSettings
    @Inject lateinit var strings: Strings

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        val configValues = FXCollections.observableArrayList(
                listOf(ConfigSetSettingsImpl.DEFAULT_ROOT_CONFIG_NAME)
                        .plus(configSetSettings.configs)
        )
        jfxProfileSelection.items = configValues
        jfxProfileSelection.isEditable = true
        val currentConfig = configSetSettings.currentConfigOrDefault()
        jfxProfileSelection.selectionModel.select(currentConfig)
        jfxButtonApply.setOnAction {
            configSetSettings.configSetName = jfxProfileSelection.value
            configSetSettings.save()
            Main.mainInstance!!.restart()
        }
        jfxInfo.text = strings.getString("profiles_info")
    }

    @PreDestroy
    fun destroy() { }

    companion object {
        val logger = LoggerFactory.getLogger(ProfilesController::class.java)!!
    }

}