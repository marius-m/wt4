package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import lt.markmerkk.*
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.interactors.AuthService
import lt.markmerkk.interactors.AuthServiceImpl
import lt.markmerkk.interactors.LogLoaderImpl
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class SettingsController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxInfo: Text
    @FXML lateinit var jfxStatusContainer: BorderPane
    @FXML lateinit var jfxStatusProgress: JFXProgressBar
    @FXML lateinit var jfxStatusButton: JFXButton
    @FXML lateinit var jfxOutputContainer: BorderPane
    @FXML lateinit var jfxOutputProgress: JFXProgressBar
    @FXML lateinit var jfxOutputTextArea: JFXTextArea
    @FXML lateinit var jfxStatusLabel: Label
    @FXML lateinit var jfxTextFieldHost: JFXTextField
    @FXML lateinit var jfxTextFieldUsername: JFXTextField
    @FXML lateinit var jfxTextFieldPassword: JFXPasswordField
    @FXML lateinit var jfxButtonTroubleshoot: JFXButton
    @FXML lateinit var jfxButtonTest: JFXButton
    @FXML lateinit var jfxButtonApply: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton

    @Inject lateinit var config: Config
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var strings: Strings
    @Inject lateinit var jiraAuthInteractor: AuthService.AuthInteractor
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var stageProperties: StageProperties

    private lateinit var authService: AuthService

    private val dialogPadding = 100.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        authService = AuthServiceImpl(
                authServiceView,
                schedulerProvider.io(),
                schedulerProvider.ui(),
                jiraAuthInteractor,
                LogLoaderImpl(config.cfgPath)
        )
        authServiceView.hideProgress()
        jfxOutputTextArea.widthProperty().addListener { _, _, newValue ->
            val width = newValue.toDouble()
            jfxOutputProgress.prefWidth = width
        }
        jfxOutputProgress.prefWidth = jfxOutputTextArea.width

        jfxInfo.text = strings.getString("settings_info")
        jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_NEUTRAL, Color.BLACK, 60.0)
        jfxStatusLabel.text = strings.getString("settings_state_neutral")
        jfxStatusLabel.isWrapText = true
        jfxTextFieldHost.text = userSettings.host
        jfxTextFieldUsername.text = userSettings.username
        jfxTextFieldPassword.text = userSettings.password
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding

        authService.onAttach()
        jfxButtonCancel.setOnAction { jfxDialog.close() }
        jfxButtonTest.setOnAction {
            authService.testLogin(
                    hostname = jfxTextFieldHost.text,
                    username = jfxTextFieldUsername.text,
                    password = jfxTextFieldPassword.text
            )
        }
        jfxStatusButton.setOnAction { jfxButtonTest.fire() }
        jfxButtonTroubleshoot.setOnAction { authService.toggleDisplayType() }
        jfxButtonApply.setOnAction { saveUserSettings() }
        showStatusContainer()
        stageProperties.register(stagePropertiesListener)
    }

    @PreDestroy
    fun destroy() {
        stageProperties.unregister(stagePropertiesListener)
        authService.onDetach()
    }

    //region Listeners

    private val authServiceView: AuthService.View = object : AuthService.View {

        override fun showProgress() {
            jfxStatusProgress.isVisible = true
            jfxStatusProgress.isManaged = true
            jfxOutputProgress.isVisible = true
            jfxOutputProgress.isManaged = true
            jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_TONGUE, Color.BLACK, 60.0)
            jfxStatusLabel.text = strings.getString("settings_state_loading")
        }

        override fun hideProgress() {
            jfxStatusProgress.isVisible = false
            jfxStatusProgress.isManaged = false
            jfxOutputProgress.isVisible = false
            jfxOutputProgress.isManaged = false
        }

        override fun showAuthResult(result: AuthService.AuthResult) {
            when (result) {
                AuthService.AuthResult.SUCCESS -> {
                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_COOL, Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_success")
                    saveUserSettings()
                    return
                }
                AuthService.AuthResult.ERROR_EMPTY_FIELDS -> {
                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_empty_fields")
                    return
                }
                AuthService.AuthResult.ERROR_UNAUTHORISED -> {
                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_unauthorised")
                    return
                }
                AuthService.AuthResult.ERROR_INVALID_HOSTNAME -> {
                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_invalid_hostname")
                    return
                }
                AuthService.AuthResult.ERROR_UNDEFINED -> {
                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_undefined")
                    return
                }
                else -> jfxStatusLabel.text = strings.getString("settings_state_error_undefined")
            }
        }

        override fun showDebugLogs() {
            showOutputContainer()
        }

        override fun hideDebugLogs() {
            showStatusContainer()
        }

        override fun fillDebugLogs(logs: String) {
            jfxOutputTextArea.text = logs
        }

        override fun errorFillingDebugLogs(throwable: Throwable) {
            logger.error("[ERROR] Cant read debug log", throwable)
            jfxOutputTextArea.text = "[ERROR] Cant read debug log"
        }

        override fun scrollToBottomOfDebugLogs(length: Int) {
            jfxOutputTextArea.positionCaret(length)
        }

    }

    private val stagePropertiesListener = object : StageProperties.StageChangeListener {
        override fun onNewWidth(newWidth: Double) {
            jfxDialogLayout.prefWidth = newWidth - dialogPadding
        }

        override fun onNewHeight(newHeight: Double) {
            jfxDialogLayout.prefHeight = newHeight - dialogPadding
        }

        override fun onFocusChange(focus: Boolean) { }
    }

    //endregion

    //region Convenience

    private fun saveUserSettings() {
        userSettings.host = jfxTextFieldHost.text
        userSettings.username = jfxTextFieldUsername.text
        userSettings.password = jfxTextFieldPassword.text
        eventBus.post(EventSnackBarMessage("Settings saved!"))
    }

    private fun showStatusContainer() {
        jfxStatusContainer.isVisible = true
        jfxStatusContainer.isManaged = true
        jfxOutputContainer.isVisible = false
        jfxOutputContainer.isManaged = false
    }

    private fun showOutputContainer() {
        jfxStatusContainer.isVisible = false
        jfxStatusContainer.isManaged = false
        jfxOutputContainer.isVisible = true
        jfxOutputContainer.isManaged = true
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(SettingsController::class.java)!!
    }

}