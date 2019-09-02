package lt.markmerkk.widgets.settings

import lt.markmerkk.GraphicsGlyph.Companion.logger
import rx.Observable
import rx.Subscription
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Responsible for controlling the state of the webview whenver
 * doing authorization steps
 * Lifecycle: [onAttach], [onDetach]
 */
// todo incomplete presenter
class AuthWebviewPresenter(
        private val view: View,
        private val authResultParser: AuthResultParser
) {

    private var subscProgress: Subscription? = null
    private var subscDocument: Subscription? = null
    private val isRunning = AtomicBoolean(false)

    fun onAttach() { }
    fun onDetach() {
        subscProgress?.unsubscribe()
        subscDocument?.unsubscribe()
    }

    fun attachRunning(isRunningAsStream: Observable<Boolean>) {
        subscProgress = isRunningAsStream
                .doOnNext {
                    isRunning.set(it)
                    if (it) {
                        view.showProgress()
                    } else {
                        view.hideProgress()
                    }
                }
                .subscribe()
    }

    fun attachDocumentProperty(documentAsStream: Observable<DocumentContent>) {
        subscDocument = documentAsStream
                .subscribe { (documentUri, documentContent) ->
                    val isLastStep = documentUri?.endsWith("/jira/plugins/servlet/oauth/authorize") ?: false
                    if (isLastStep) {
                        val accessToken = authResultParser.findAccessToken(documentContent ?: "")
                        view.onAuthSuccess(accessToken)
                    }
                }
    }

    data class DocumentContent(val documentUri: String, val documentContent: String)

    interface View {
        fun onAuthSuccess(authToken: String)
        fun onAuthFailure()
        fun showProgress()
        fun hideProgress()
    }

}