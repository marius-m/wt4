package lt.markmerkk

import com.sun.javafx.application.HostServicesDelegate
import javafx.application.Application
import lt.markmerkk.mvp.HostServicesInteractor

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
class HostServicesInteractorImpl(
        private val application: Application,
        private val userSettings: UserSettings
) : HostServicesInteractor {

    private val hostServices = HostServicesDelegate.getInstance(application)

    override fun openExternalIssue(issue: String) {
        if (userSettings.host.isEmpty()) return
        hostServices?.showDocument("${userSettings.host}/browse/$issue")
    }

    override fun openExternalLink(rawLink: String) {
        hostServices?.showDocument(rawLink)
    }

}