package lt.markmerkk.mvp

interface HostServicesInteractor {
    fun ticketWebLinkToClipboard(webLinkToTicket: String)
    fun openLink(link: String)
}