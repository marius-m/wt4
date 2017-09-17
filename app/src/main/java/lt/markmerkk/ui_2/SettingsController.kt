package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
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

/**
 * @author mariusmerkevicius
 * @since 2017-09-12
 */
class SettingsController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxInfo: Text
    @FXML lateinit var jfxSpinner: JFXSpinner
    @FXML lateinit var jfxTextFieldHost: JFXTextField
    @FXML lateinit var jfxTextFieldUsername: JFXTextField
    @FXML lateinit var jfxTextFieldPassword: JFXTextField
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
        authService.onAttach()
        jfxButtonCancel.setOnAction { jfxDialog.close() }
        jfxButtonTest.setOnAction {
            authService.testLogin(
                    hostname = jfxTextFieldHost.text,
                    username = jfxTextFieldUsername.text,
                    password = jfxTextFieldPassword.text
            )
        }
    }

    @PreDestroy
    fun destroy() {
        authService.onDetach()
    }

    //region Listeners

    private val authServiceView: AuthService.View = object : AuthService.View {

        override fun showProgress() {
            jfxSpinner.isVisible = true
            jfxSpinner.isManaged = true
        }

        override fun hideProgress() {
            jfxSpinner.isVisible = false
            jfxSpinner.isManaged = false
        }

        override fun showAuthSuccess() {
            println("yay!")
        }

        override fun showAuthFailUnauthorised(error: Throwable) {
            println("boo!: Unauthorised!")
        }

        override fun showAuthFailInvalidHostname(error: Throwable) {
            println("boo!: Invalid hostname!: ${error}")
        }

        override fun showAuthFailInvalidUndefined(error: Throwable) {
            println("boo!: Not sure!: ${error}")
        }

        override fun showDebugLogs() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun hideDebugLogs() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    //endregion

}