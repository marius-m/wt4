package lt.markmerkk.widgets.settings

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.svg.SVGGlyph
import io.sentry.Attachment
import io.sentry.Scope
import io.sentry.Sentry
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import lt.markmerkk.*
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.interactors.AuthService
import lt.markmerkk.interactors.JiraBasicApi
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSpinner
import lt.markmerkk.ui_2.views.jfxTextArea
import org.slf4j.LoggerFactory
import rx.Observable
import rx.observables.JavaFxObservable
import tornadofx.*
import java.io.File
import javax.inject.Inject

// todo incomplete display of behaviour
class AccountSettingsOauthWidget : Fragment() {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var jiraAuthInteractor: AuthService.AuthInteractor
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var strings: Strings
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var jiraClientProvider: JiraClientProvider
    @Inject lateinit var appConfig: Config
    @Inject lateinit var jiraBasicApi: JiraBasicApi
    @Inject lateinit var autoSyncWatcher: AutoSyncWatcher2
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var viewWebview: WebView
    private lateinit var viewButtonStatus: JFXButton
    private lateinit var viewButtonSetupConnection: JFXButton
    private lateinit var viewLabelStatus: Label
    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewTextOutput: JFXTextArea
    private lateinit var viewContainerLogs: BorderPane
    private lateinit var viewContainerSetup: VBox
    private lateinit var viewContainerSetupStatus: VBox
    private lateinit var viewContainerSetupWebview: VBox

    private lateinit var logFile: File

    init {
        Main.component().inject(this)
    }

    private lateinit var authorizator: OAuthAuthorizator
    private lateinit var authWebviewPresenter: AuthWebviewPresenter
    private lateinit var logTailer: LogTailer

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
        center {
            borderpane {
                minWidth = 650.0
                minHeight = 450.0
                center {
                    stackpane {
                        viewContainerSetup = vbox(spacing = 4, alignment = Pos.CENTER) {
                            viewContainerSetupStatus = vbox(spacing = 4, alignment = Pos.CENTER) {
                                viewButtonStatus = jfxButton {
                                    setOnAction { authorizator.checkAuth() }
                                }
                                viewLabelStatus = label {
                                    alignment = Pos.CENTER
                                    isWrapText = true
                                }
                                viewButtonSetupConnection = jfxButton("Set-up new connection".toUpperCase()) {
                                    addClass(Styles.dialogButtonAction)
                                    setOnAction { authorizator.setupAuthStep1() }
                                }
                            }
                            viewContainerSetupWebview = vbox {
                                viewWebview = webview { }
                            }
                        }
                        viewContainerLogs = borderpane {
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
        authorizator = OAuthAuthorizator(
                view = object : OAuthAuthorizator.View {
                    override fun accountReady() {
                        autoSyncWatcher.markForShortCycleUpdate()
                    }

                    override fun renderView(authViewModel: AuthViewModel) {
                        viewContainerSetupStatus.showIf(authViewModel.showContainerStatus)
                        viewContainerSetupWebview.showIf(authViewModel.showContainerWebview)
                        viewButtonSetupConnection.showIf(authViewModel.showButtonSetupNew)
                        viewButtonStatus.graphic = when (authViewModel.showStatusEmoticon) {
                            AuthViewModel.StatusEmoticon.HAPPY -> graphics.from(Glyph.EMOTICON_COOL, Color.BLACK, 64.0)
                            AuthViewModel.StatusEmoticon.NEUTRAL -> graphics.from(Glyph.EMOTICON_NEUTRAL, Color.BLACK, 64.0)
                            AuthViewModel.StatusEmoticon.SAD -> graphics.from(Glyph.EMOTICON_DEAD, Color.BLACK, 64.0)
                        }
                        viewLabelStatus.text = authViewModel.textStatus
                    }

                    override fun loadAuthWeb(url: String) {
                        viewWebview.engine.load(url)
                    }

                    override fun resetWeb() {
                        // Cookie reset does not work, as it scrambles the login flow
                        // Source: https://stackoverflow.com/questions/23409138/clear-the-session-cache-cookie-in-the-javafx-webview
//                        java.net.CookieHandler.setDefault(java.net.CookieManager())
                        viewWebview.engine.loadContent("<html></html>")
                    }

                    override fun showProgress() {
                        viewProgress.show()
                    }

                    override fun hideProgress() {
                        viewProgress.hide()
                    }

                },
                oAuthInteractor = OAuthInteractor(userSettings),
                jiraClientProvider = jiraClientProvider,
                jiraApi = jiraBasicApi,
                userSettings = userSettings,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        authWebviewPresenter = AuthWebviewPresenter(
                view = object : AuthWebviewPresenter.View {
                    override fun onAccessToken(accessTokenKey: String) {
                        authorizator.setupAuthStep2(accessTokenKey)
                    }

                    override fun showProgress() {
                        viewProgress.show()
                    }

                    override fun hideProgress() {
                        viewProgress.hide()
                    }
                },
                authResultParser = AuthResultParser()
        )
        viewContainerSetup.show()
        viewContainerLogs.hide()

        logTailer = LogTailer(
                logTailerLister,
                schedulerProvider.io(),
                schedulerProvider.ui()
        )
        logTailer.onAttach()
        authorizator.onAttach()
        authWebviewPresenter.onAttach()
        authWebviewPresenter.attachRunning(JavaFxObservable.valuesOf(viewWebview.engine.loadWorker.runningProperty()))
        val wvDocumentProperty = JavaFxObservable.valuesOf(viewWebview.engine.documentProperty())
                .flatMap {
                    if (it != null) {
                        val documentUri: String = it.documentURI ?: ""
                        Observable.just(AuthWebviewPresenter.DocumentContent(documentUri, it.documentElement.textContent))
                    } else {
                        Observable.empty()
                    }
                }
        authWebviewPresenter.attachDocumentProperty(wvDocumentProperty)
        autoSyncWatcher.changeUpdateLock(isInLock = true, lockProcessName = "AccountSettingsOauthWidget")
    }

    override fun onUndock() {
        autoSyncWatcher.changeUpdateLock(isInLock = false, lockProcessName = "")
        authWebviewPresenter.onDetach()
        authorizator.onDetach()
        logTailer.onDetach()
        viewTextOutput.clear()
        super.onUndock()
    }

    private fun toggleAdvanced() {
        if (viewContainerLogs.isVisible) {
            viewContainerLogs.hide()
            viewContainerSetup.show()
            logTailer.clear()
        } else {
            viewContainerLogs.show()
            viewContainerSetup.hide()
            this.logFile = File(appConfig.basePath(), "${File.separator}logs${File.separator}app.log")
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

    private val logTailerLister = object : LogTailer.Listener {

        override fun onLogUpdate(logAsString: String) {
            viewTextOutput.appendText("$logAsString\n")
        }

        override fun onClearLog() {
            viewTextOutput.clear()
        }
    }

    //endregion

    //region Listeners



    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}