package lt.markmerkk.mvp

interface HostServicesInteractor {
    fun copyText(inputText: String)
    fun ticketWebLinkToClipboard(webLinkToTicket: String)
    fun openLink(link: String)
}