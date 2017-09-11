package lt.markmerkk

import com.sun.javafx.application.HostServicesDelegate
import javafx.application.Application
import javafx.scene.input.Clipboard
import lt.markmerkk.mvp.HostServicesInteractor
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Clipboard.getSystemClipboard



/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
class HostServicesInteractorImpl(
        private val application: Application,
        private val userSettings: UserSettings
) : HostServicesInteractor {

    override fun generateLink(issue: String): String {
        return "${userSettings.host}/browse/$issue"
    }

    private val hostServices = HostServicesDelegate.getInstance(application)

    override fun openExternalIssue(issue: String) {
        if (userSettings.host.isEmpty()) return
        hostServices?.showDocument(generateLink(issue))
    }

    override fun copyLinkToClipboard(issue: String) {
        val clipboard = Clipboard.getSystemClipboard()
        val content = ClipboardContent()
        content.putString(generateLink(issue))
        clipboard.setContent(content)
    }

    override fun openExternalLink(rawLink: String) {
        hostServices?.showDocument(rawLink)
    }

}