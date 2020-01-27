package lt.markmerkk

import com.sun.javafx.application.HostServicesDelegate
import javafx.application.Application
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.widgets.main.MainWidget
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL


class HostServicesInteractorImpl(
        private val application: Application,
        private val userSettings: UserSettings
) : HostServicesInteractor {

    override fun ticketWebLinkToClipboard(webLinkToTicket: String) {
        val clipboard = Clipboard.getSystemClipboard()
        val content = ClipboardContent()
        content.putString(webLinkToTicket)
        clipboard.setContent(content)
    }

    override fun openLink(link: String) {
        try {
            Desktop.getDesktop().browse(URL(link).toURI())
        } catch (e: IOException) {
            logger.warn("Cannot open link $link", e)
        } catch (e: URISyntaxException) {
            logger.warn("Cannot open link $link", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HostServicesInteractorImpl::class.java)!!
    }

}