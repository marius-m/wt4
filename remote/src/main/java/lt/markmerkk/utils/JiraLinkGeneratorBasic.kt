package lt.markmerkk.utils

import lt.markmerkk.entities.TicketCode
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscription

/**
 * Responsible for handling jira link to ticket generation
 */
class JiraLinkGeneratorBasic(
        private val view: JiraLinkGenerator.View?,
        private val accountAvailablility: AccountAvailablility
): JiraLinkGenerator {

    private var subsInputTicketCode: Subscription? = null

    override fun onAttach() { }
    override fun onDetach() {
        subsInputTicketCode?.unsubscribe()
    }

    override fun attachTicketCodeInput(inputTicketCodeAsStream: Observable<String>) {
        subsInputTicketCode = inputTicketCodeAsStream
                .subscribe({
                    handleTicketInput(it)
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
    }

    override fun webLinkFromInput(ticketCodeAsString: String): String {
        val host = accountAvailablility.host()
        if (host.isEmpty()) {
            return ""
        }
        val ticketCode = TicketCode.new(ticketCodeAsString)
        if (!ticketCode.isEmpty()) {
            return JiraLinkGenerator.webLinkFromCode(host, ticketCode)
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
        private val logger = LoggerFactory.getLogger(JiraLinkGeneratorBasic::class.java)!!
    }

}