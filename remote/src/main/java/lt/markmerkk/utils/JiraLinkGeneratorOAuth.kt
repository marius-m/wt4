package lt.markmerkk.utils

import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketCode
import rx.Observable
import rx.Subscription

/**
 * Responsible for handling jira link to ticket generation
 */
class JiraLinkGeneratorOAuth(
        private val view: JiraLinkGenerator.View?,
        private val userSettings: UserSettings
): JiraLinkGenerator {

    private var subsInputTicketCode: Subscription? = null

    override fun onAttach() { }
    override fun onDetach() {
        subsInputTicketCode?.unsubscribe()
    }

    override fun attachTicketCodeInput(inputTicketCodeAsStream: Observable<String>) {
        subsInputTicketCode = inputTicketCodeAsStream
                .subscribe { handleTicketInput(it) }
    }

    override fun webLinkFromInput(ticketCodeAsString: String): String {
        if (userSettings.jiraUser().isEmpty()
                || userSettings.jiraOAuthCreds().isEmpty()
                || userSettings.jiraOAuthPreset().isEmpty()) {
            return ""
        }
        val ticketCode = TicketCode.new(ticketCodeAsString)
        if (!ticketCode.isEmpty()) {
            return JiraLinkGenerator.webLinkFromCode(
                    userSettings.jiraOAuthPreset().host,
                    ticketCode
            )
        }
        return ""
    }

    override fun handleTicketInput(ticketCodeAsString: String) {
        val webLink = webLinkFromInput(ticketCodeAsString)
        if (webLink.isNotEmpty()) {
            view?.showCopyLink(TicketCode.new(ticketCodeAsString), webLink)
        } else {
            view?.hideCopyLink()
        }
    }

}