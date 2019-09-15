package lt.markmerkk

import com.sun.javafx.application.HostServicesDelegate
import javafx.application.Application
import javafx.scene.input.Clipboard
import lt.markmerkk.mvp.HostServicesInteractor
import javafx.scene.input.ClipboardContent


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

}