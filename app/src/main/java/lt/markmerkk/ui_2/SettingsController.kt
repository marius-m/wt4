package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.Strings
import lt.markmerkk.mvp.AuthService
import lt.markmerkk.mvp.AuthServiceImpl
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.swing.text.html.ImageView

/**
 * @author mariusmerkevicius
 * @since 2017-09-12
 */
class SettingsController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxInfo: Text
    @FXML lateinit var jfxStatusProgress: JFXProgressBar
    @FXML lateinit var jfxStatusButton: JFXButton
    @FXML lateinit var jfxStatusLabel: Label
    @FXML lateinit var jfxTextFieldHost: JFXTextField
    @FXML lateinit var jfxTextFieldUsername: JFXTextField
    @FXML lateinit var jfxTextFieldPassword: JFXPasswordField
    @FXML lateinit var jfxButtonTroubleshoot: JFXButton
    @FXML lateinit var jfxButtonTest: JFXButton
    @FXML lateinit var jfxButtonApply: JFXButton
    @FXML lateinit var jfxButtonCancel: JFXButton

    @Inject lateinit var strings: Strings
    @Inject lateinit var jiraAuthInteractor: AuthService.AuthInteractor
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    lateinit var authService: AuthService

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)
        authService = AuthServiceImpl(
                authServiceView,
                Schedulers.io(),
                JavaFxScheduler.getInstance(),
                jiraAuthInteractor
        )
        authServiceView.hideProgress()

        jfxInfo.text = strings.getString("settings_info")
        jfxStatusButton.graphic = graphics.glyph("emoticon_neutral", Color.BLACK, 60.0)
        jfxStatusLabel.text = strings.getString("settings_state_neutral")
        jfxStatusLabel.isWrapText = true

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
    }

    @PreDestroy
    fun destroy() {
        authService.onDetach()
    }

    //region Listeners

    private val authServiceView: AuthService.View = object : AuthService.View {

        override fun showProgress() {
            jfxStatusProgress.isVisible = true
            jfxStatusProgress.isManaged = true
            jfxStatusButton.graphic = graphics.glyph("emoticon_tongue", Color.BLACK, 60.0)
            jfxStatusLabel.text = strings.getString("settings_state_loading")
        }

        override fun hideProgress() {
            jfxStatusProgress.isVisible = false
            jfxStatusProgress.isManaged = false
        }

        override fun showAuthResult(result: AuthService.AuthResult) {
            when (result) {
                AuthService.AuthResult.SUCCESS -> {
                    jfxStatusButton.graphic = graphics.glyph("emoticon_cool", Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_success")
                    return
                }
                AuthService.AuthResult.ERROR_EMPTY_FIELDS -> {
                    jfxStatusButton.graphic = graphics.glyph("emoticon_dead", Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_empty_fields")
                    return
                }
                AuthService.AuthResult.ERROR_UNAUTHORISED -> {
                    jfxStatusButton.graphic = graphics.glyph("emoticon_dead", Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_unauthorised")
                    return
                }
                AuthService.AuthResult.ERROR_INVALID_HOSTNAME -> {
                    jfxStatusButton.graphic = graphics.glyph("emoticon_dead", Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_invalid_hostname")
                    return
                }
                AuthService.AuthResult.ERROR_UNDEFINED -> {
                    jfxStatusButton.graphic = graphics.glyph("emoticon_dead", Color.BLACK, 60.0)
                    jfxStatusLabel.text = strings.getString("settings_state_error_undefined")
                    return
                }
                else -> jfxStatusLabel.text = strings.getString("settings_state_error_undefined")
            }
        }

        override fun showDebugLogs() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun hideDebugLogs() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    //endregion

    //region Convenience

    private fun centerInAnchorPane(node: Node) {
        AnchorPane.setTopAnchor(node, 10.0)
        AnchorPane.setBottomAnchor(node, 10.0)
        AnchorPane.setLeftAnchor(node, 10.0)
        AnchorPane.setRightAnchor(node, 10.0)
    }

    //endregion

}