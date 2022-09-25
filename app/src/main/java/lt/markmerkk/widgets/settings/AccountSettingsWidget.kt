package lt.markmerkk.widgets.settings

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import io.sentry.Attachment
import io.sentry.Scope
import io.sentry.Sentry
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.interactors.AuthService
import lt.markmerkk.interactors.AuthServiceImpl
import lt.markmerkk.ui_2.views.*
import tornadofx.*
import java.io.File
import javax.inject.Inject

class AccountSettingsWidget : Fragment() {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var jiraAuthInteractor: AuthService.AuthInteractor
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var strings: Strings
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var appConfig: Config
    @Inject lateinit var autoSyncWatcher: AutoSyncWatcher2
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var viewInputHostname: JFXTextField
    private lateinit var viewInputUsername: JFXTextField
    private lateinit var viewInputPassword: JFXPasswordField
    private lateinit var viewButtonStatus: JFXButton
    private lateinit var viewLabelStatus: Label
    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewTextOutput: JFXTextArea
    private lateinit var viewContainerStatusAdvanced: BorderPane
    private lateinit var viewContainerStatusBasic: VBox

    private lateinit var logFile: File

    init {
        Main.component().inject(this)
    }

    private lateinit var logTailer: LogTailer
    private lateinit var authService: AuthService

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Account settings") {
                    addClass(Styles.dialogHeader)
                }
                viewProgress = jfxSpinner {
                    style {
                        padding = box(all = 4.px)
                    }
                    val boxDimen = 42.0
                    minWidth = boxDimen
                    maxWidth = boxDimen
                    minHeight = boxDimen
                    maxHeight = boxDimen
                    hide()
                }
            }
        }
        left {
            vbox(spacing = 20) {
                style {
                    padding = box(
                            top = 0.px,
                            left = 0.px,
                            right = 4.px,
                            bottom = 0.px
                    )
                }
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
                    promptText = "Password / API token"
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
                                graphic = graphics.from(Glyph.EMOTICON_NEUTRAL, Color.BLACK, 64.0)
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
                                        hBarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
                                        vBarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
                                    }
                                    isEditable = false
                                    isWrapText = false
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
                jfxButton("Send report".toUpperCase()) {
                    setOnAction {
                        sendReport()
                        eventBus.post(EventSnackBarMessage("Thank you for the report!"))
                        close()
                    }
                }
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
                jfxButton("Save".toUpperCase()) {
                    setOnAction {
                        userSettings.changeBasicCreds(
                                hostname = viewInputHostname.text,
                                username = viewInputUsername.text,
                                password = viewInputPassword.text
                        )
                        eventBus.post(EventSnackBarMessage("Settings saved!"))
                        close()
                    }
                }
                jfxButton("Close".toUpperCase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        this.logFile = File(appConfig.basePath(), "${File.separator}logs${File.separator}app.log")
        viewContainerStatusBasic.show()
        viewContainerStatusAdvanced.hide()
        val jiraBasicCreds = userSettings.jiraBasicCreds()
        viewInputHostname.text = jiraBasicCreds.host
        viewInputUsername.text = jiraBasicCreds.username
        viewInputPassword.text = jiraBasicCreds.password

        authService = AuthServiceImpl(
                authServiceView,
                schedulerProvider.io(),
                schedulerProvider.ui(),
                jiraAuthInteractor,
                userSettings
        )
        logTailer = LogTailer(
                logTailerLister,
                schedulerProvider.io(),
                schedulerProvider.ui()
        )
        authService.onAttach()
        logTailer.onAttach()
        autoSyncWatcher.changeUpdateLock(isInLock = true, lockProcessName = "AccountSettingsWidget")
    }

    override fun onUndock() {
        autoSyncWatcher.changeUpdateLock(isInLock = false, lockProcessName = "")
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
            logTailer.tail(logFile)
        }
    }

    private fun sendReport() {
        val attachment = Attachment(logFile.absolutePath)
        Sentry.withScope { scope: Scope ->
            scope.addAttachment(attachment)
            Sentry.captureMessage("Sentry report (${timeProvider.now()})");
        }
    }

    //region Logs

    private val authServiceView: AuthService.View = object : AuthService.View {

        override fun showProgress() {
            viewProgress.show()
            viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_TONGUE, Color.BLACK, 60.0)
            viewLabelStatus.text = strings.getString("settings_state_loading")
        }

        override fun hideProgress() {
            viewProgress.hide()
        }

        override fun showAuthResult(result: AuthService.AuthResult) {
            when (result) {
                AuthService.AuthResult.SUCCESS -> {
                    viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_COOL, Color.BLACK, 60.0)
                    viewLabelStatus.text = strings.getString("settings_state_success")
                    autoSyncWatcher.markForShortCycleUpdate()
                }
                AuthService.AuthResult.ERROR_EMPTY_FIELDS -> {
                    viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    viewLabelStatus.text = strings.getString("settings_state_error_empty_fields")
                }
                AuthService.AuthResult.ERROR_UNAUTHORISED -> {
                    viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    viewLabelStatus.text = strings.getString("settings_state_error_unauthorised")
                }
                AuthService.AuthResult.ERROR_INVALID_HOSTNAME -> {
                    viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    viewLabelStatus.text = strings.getString("settings_state_error_invalid_hostname")
                }
                AuthService.AuthResult.ERROR_UNDEFINED -> {
                    viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    viewLabelStatus.text = strings.getString("settings_state_error_undefined")
                }
                else -> {
                    viewButtonStatus.graphic = graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 60.0)
                    viewLabelStatus.text = strings.getString("settings_state_error_undefined")
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