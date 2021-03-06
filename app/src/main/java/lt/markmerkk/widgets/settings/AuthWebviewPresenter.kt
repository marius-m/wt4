package lt.markmerkk.widgets.settings

import lt.markmerkk.GraphicsGlyph.Companion.logger
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
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
                .subscribe({ (documentUri, documentContent) ->
                    logger.debug("Loading uri: $documentUri")
                    val isLastStep = documentUri.endsWith(AUTH_URI)
                    if (isLastStep) {
                        val accessToken = authResultParser.findAccessToken(documentContent)
                        view.onAccessToken(accessToken)
                    }
                }, {
                    logger.warn("Error trying to resolve access code", it)
                    view.onAccessToken("")
                })
    }

    data class DocumentContent(val documentUri: String, val documentContent: String)

    interface View {
        fun onAccessToken(accessTokenKey: String)
        fun showProgress()
        fun hideProgress()
    }

    companion object {
        const val AUTH_URI = "plugins/servlet/oauth/authorize"
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}