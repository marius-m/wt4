package lt.markmerkk.utils

import lt.markmerkk.entities.TicketCode
import rx.Observable

interface JiraLinkGenerator {
    fun onAttach()
    fun onDetach()

    /**
     * @return generates a valid web link or empty
     */
    fun webLinkFromInput(ticketCodeAsString: String): String
    fun handleTicketInput(ticketCodeAsString: String)

    interface View {
        fun showCopyLink(ticketCode: TicketCode, webLink: String)
        fun hideCopyLink()
    }

    companion object {
        fun webLinkFromCode(
                host: String,
                ticketCode: TicketCode
        ): String {
            if (host.isEmpty()
                    || ticketCode.isEmpty()) {
                return ""
            }
            val hostSanitized = if (host.endsWith("/")) {
                host.substring(0, host.length - 1)
            } else {
                host
            }
            return "$hostSanitized/browse/${ticketCode.code}"
        }
    }
}