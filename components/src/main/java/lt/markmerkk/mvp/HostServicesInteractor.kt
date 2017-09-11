package lt.markmerkk.mvp

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
interface HostServicesInteractor {

    fun generateLink(issue: String): String
    /**
     * Will open link in browser with issue generated link
     */
    fun openExternalIssue(issue: String)

    fun openExternalLink(rawLink: String)

    fun copyLinkToClipboard(issue: String)

}