package lt.markmerkk.utils

import lt.markmerkk.entities.TicketCode
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscription

/**
 * Responsible for handling jira link to ticket generation
 */
class JiraLinkGeneratorOAuth(
        private val view: JiraLinkGenerator.View?,
        private val accountAvailability: AccountAvailablility
) : JiraLinkGenerator {

    override fun onAttach() {}
    override fun onDetach() { }

    override fun webLinkFromInput(ticketCodeAsString: String): String {
        if (!accountAvailability.isAccountReadyForSync()) {
            return ""
        }
        val ticketCode = TicketCode.new(ticketCodeAsString)
        if (!ticketCode.isEmpty()) {
            return JiraLinkGenerator.webLinkFromCode(
                    accountAvailability.host(),
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

    companion object {
        private val logger = LoggerFactory.getLogger(JiraLinkGeneratorOAuth::class.java)!!
    }

}