package lt.markmerkk.widgets.settings

import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.interactors.AuthService
import lt.markmerkk.interactors.AuthServiceImpl
import lt.markmerkk.interactors.LogLoaderImpl
import lt.markmerkk.ui_2.views.*
import tornadofx.*
import java.io.File
import javax.inject.Inject

class AccountSettingsWidget : View() {

    @Inject lateinit var grahics: Graphics<SVGGlyph>
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var jiraAuthInteractor: AuthService.AuthInteractor
    @Inject lateinit var userSettings: UserSettings

    private lateinit var viewInputHostname: JFXTextField
    private lateinit var viewInputUsername: JFXTextField
    private lateinit var viewInputPassword: JFXPasswordField
    private lateinit var viewButtonStatus: JFXButton
    private lateinit var viewLabelStatus: Label
    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewTextOutput: JFXTextArea
    private lateinit var viewContainerStatusAdvanced: BorderPane
    private lateinit var viewContainerStatusBasic: VBox

    init {
        Main.component().inject(this)
    }

    private lateinit var logTailer: LogTailer
    private lateinit var authService: AuthService

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.CENTER_LEFT) {
                label("Account settings") {
                    addClass(Styles.dialogHeader)
                }
                viewProgress = jfxSpinner {
                    minWidth = 24.0
                    maxWidth = 24.0
                    hide()
                }
            }
        }
        left {
            vbox(spacing = 20) {
                minWidth = 160.0
                viewInputHostname = jfxTextField {
                    addClass(Styles.inputTextField)
                    focusColor = Styles.cActiveRed
                    isLabelFloat = true
                    promptText = "Hostname"
                    unFocusColor = Color.BLACK
                }
                viewInputUsername = jfxTextField {
                    addClass(Styles.inputTextField)
                    focusColor = Styles.cActiveRed
                    isLabelFloat = true
                    promptText = "Username"
                    unFocusColor = Color.BLACK
                }
                viewInputPassword = jfxPassField {
                    addClass(Styles.inputTextField)
                    focusColor = Styles.cActiveRed
                    isLabelFloat = true
                    promptText = "Password"
                    unFocusColor = Color.BLACK
                }
                text {
                    addClass(Styles.labelMini)
                    wrappingWidth = 160.0
                }
            }
        }
        center {
            borderpane {
                minWidth = 300.0
                minHeight = 300.0
                center {
                    stackpane {
                        viewContainerStatusBasic = vbox(spacing = 4, alignment = Pos.CENTER) {
                            viewButtonStatus = jfxButton {
                                graphic = grahics.from(Glyph.EMOTICON_NEUTRAL, Color.BLACK, 64.0)
                                setOnAction {
                                    authService.testLogin(
                                            hostname = viewInputHostname.text,
                                            username = viewInputUsername.text,
                                            password = viewInputPassword.text
                                    )
                                }
                            }
                            viewLabelStatus = label {  }
                        }
                        viewContainerStatusAdvanced = borderpane {
                            center {
                                viewTextOutput = jfxTextArea {
                                    style {
                                        fontSize = 8.pt
                                        fontFamily = "monospaced"
                                    }
                                    isEditable = false
                                }
                            }
                        }
                    }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Show logs".toUpperCase()) {
                    setOnAction { toggleAdvanced() }
                }
                jfxButton("Test connection".toUpperCase()) {
                    setOnAction {
                        authService.testLogin(
                                hostname = viewInputHostname.text,
                                username = viewInputUsername.text,
                                password = viewInputPassword.text
                        )
                    }
                }
                jfxButton("Save".toUpperCase()) { }
                jfxButton("Dismiss".toUpperCase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        viewContainerStatusBasic.show()
        viewContainerStatusAdvanced.hide()
        viewInputHostname.text = userSettings.host
        viewInputUsername.text = userSettings.username
        viewInputPassword.text = userSettings.password

        authService = AuthServiceImpl(
                authServiceView,
                schedulerProvider.io(),
                schedulerProvider.ui(),
                jiraAuthInteractor
        )
        logTailer = LogTailer(
                logTailerLister,
                schedulerProvider.io(),
                schedulerProvider.ui()
        )
        authService.onAttach()
        logTailer.onAttach()
    }

    override fun onUndock() {
        authService.onDetach()
        logTailer.onDetach()
        viewTextOutput.clear()
        super.onUndock()
    }

    private fun toggleAdvanced() {
        if (viewContainerStatusAdvanced.isVisible) {
            viewContainerStatusAdvanced.hide()
            viewContainerStatusBasic.show()
            logTailer.clear()
        } else {
            viewContainerStatusAdvanced.show()
            viewContainerStatusBasic.hide()
            logTailer.tail(File("logs/jira.log"))
        }
    }

    //region Logs

    private val authServiceView: AuthService.View = object : AuthService.View {

        override fun showProgress() {
            viewProgress.show()
//            jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_TONGUE, Color.BLACK, 60.0)
//            jfxStatusLabel.text = strings.getString("settings_state_loading")
        }

        override fun hideProgress() {
            viewProgress.hide()
//            jfxStatusProgress.isVisible = false
//            jfxStatusProgress.isManaged = false
//            jfxOutputProgress.isVisible = false
//            jfxOutputProgress.isManaged = false
        }

        override fun showAuthResult(result: AuthService.AuthResult) {
            when (result) {
                AuthService.AuthResult.SUCCESS -> {
//                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_COOL, Color.BLACK, 60.0)
//                    jfxStatusLabel.text = strings.getString("settings_state_success")
//                    saveUserSettings()
//                    return
                }
                AuthService.AuthResult.ERROR_EMPTY_FIELDS -> {
//                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
//                    jfxStatusLabel.text = strings.getString("settings_state_error_empty_fields")
//                    return
                }
                AuthService.AuthResult.ERROR_UNAUTHORISED -> {
//                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
//                    jfxStatusLabel.text = strings.getString("settings_state_error_unauthorised")
//                    return
                }
                AuthService.AuthResult.ERROR_INVALID_HOSTNAME -> {
//                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
//                    jfxStatusLabel.text = strings.getString("settings_state_error_invalid_hostname")
//                    return
                }
                AuthService.AuthResult.ERROR_UNDEFINED -> {
//                    jfxStatusButton.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
//                    jfxStatusLabel.text = strings.getString("settings_state_error_undefined")
//                    return
                }
                else -> {
//                    jfxStatusLabel.text = strings.getString("settings_state_error_undefined")
                }
            }
        }

    }

    private val logTailerLister = object : LogTailer.Listener {

        override fun onLogUpdate(logAsString: String) {
            viewTextOutput.appendText("$logAsString\n")
        }

        override fun onClearLog() {
            viewTextOutput.clear()
        }
    }

    //endregion

}