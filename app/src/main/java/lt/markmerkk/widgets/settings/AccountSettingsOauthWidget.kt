package lt.markmerkk.widgets.settings

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import lt.markmerkk.*
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.interactors.AuthService
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
class AccountSettingsOauthWidget : View() {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var jiraAuthInteractor: AuthService.AuthInteractor
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var strings: Strings
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var jiraClientProvider: JiraClientProvider

    private lateinit var viewWebview: WebView
    private lateinit var viewLabelStatus: Label
    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewTextOutput: JFXTextArea
    private lateinit var viewContainerStatusAdvanced: BorderPane
    private lateinit var viewContainerStatusBasic: VBox

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
                minWidth = 300.0
                minHeight = 300.0
                center {
                    stackpane {
                        viewContainerStatusBasic = vbox(spacing = 4, alignment = Pos.CENTER) {
                            viewWebview = webview {  }
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
                jfxButton("Show logs".toUpperCase()) {
                    setOnAction { toggleAdvanced() }
                }
                jfxButton("Set-up account".toUpperCase()) {
                    setOnAction {
                        authorizator.setupAuthStep1()
                    }
                }
                jfxButton("Save".toUpperCase()) {
                    setOnAction {
                        eventBus.post(EventSnackBarMessage("Settings saved!"))
                        close()
                    }
                }
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
        authorizator = OAuthAuthorizator(
                webview = object : OAuthAuthorizator.AuthWebView {
                    override fun loadAuth(url: String) {
                        viewWebview.engine.load(url)
                    }
                },
                view = object : OAuthAuthorizator.View {
                    override fun showAuthSuccess() {
                        // incomplete behaviour
                    }

                    override fun showAuthFailure() {
                        // incomplete behaviour
                    }

                    override fun showProgress() {
                        viewProgress.show()
                    }

                    override fun hideProgress() {
                        viewProgress.hide()
                    }

                    override fun onError(throwable: Throwable) {
                        error(header = "Error", content = "Unexpected error when trying to authorize")
                    }
                },
                oAuthInteractor = OAuthInteractor(userSettings),
                jiraClientProvider = jiraClientProvider,
                userSettings = userSettings,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        authWebviewPresenter = AuthWebviewPresenter(
                view = object : AuthWebviewPresenter.View {
                    override fun onAuthSuccess(authToken: String) {
                        authorizator.setupAuthStep2(authToken)
                    }

                    override fun onAuthFailure() {
                        error(header = "Error", content = "Failure setting up authorization")
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
        viewContainerStatusBasic.show()
        viewContainerStatusAdvanced.hide()

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
                        Observable.just(AuthWebviewPresenter.DocumentContent(it.documentURI, it.documentElement.textContent))
                    } else {
                        Observable.empty()
                    }
                }
        authWebviewPresenter.attachDocumentProperty(wvDocumentProperty)
    }

    override fun onUndock() {
        authWebviewPresenter.onDetach()
        authorizator.onDetach()
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