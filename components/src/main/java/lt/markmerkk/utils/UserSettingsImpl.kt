package lt.markmerkk.utils

import lt.markmerkk.Const
import lt.markmerkk.UserSettings
import org.slf4j.LoggerFactory

/**
 * Controller for holding persistent data
 */
class UserSettingsImpl(
        private val settings: HashSettings
) : UserSettings {

    override fun onAttach() {
        settings.load()
        host = settings.get(HOST, "")
        username = settings.get(USER, "")
        password = settings.get(PASS, "")
        version = settings.getInt(VERSION, -1)
        autoUpdateMinutes = settings.getLong(AUTOUPDATE_TIMEOUT, -1).toInt()
        lastUpdate = settings.getLong(LAST_UPDATE, -1)
        issueJql = settings.get(ISSUE_JQL, Const.DEFAULT_JQL_USER_ISSUES)
        ticketLastUpdate = settings.getLong(TICKET_LAST_UPDATE, -1)
    }

    override fun onDetach() {
        settings.set(HOST, host)
        settings.set(USER, username)
        settings.set(PASS, password)
        settings.set(VERSION, version.toString())
        settings.set(ISSUE_JQL, issueJql)
        settings.set(AUTOUPDATE_TIMEOUT, autoUpdateMinutes.toString())
        settings.set(LAST_UPDATE, lastUpdate.toString())
        settings.set(TICKET_LAST_UPDATE, ticketLastUpdate.toString())
        settings.save()
    }

    override var host: String = ""
    override var username: String = ""
    override var password: String = ""
    override var issueJql: String = Const.DEFAULT_JQL_USER_ISSUES
    override var version = -1
    override var autoUpdateMinutes: Int = -1
    override var lastUpdate: Long = -1
    override var ticketLastUpdate: Long = -1

    companion object {
        const val HOST = "HOST"
        const val USER = "USER"
        const val PASS = "PASS"
        const val VERSION = "VERSION"
        const val ISSUE_JQL = "ISSUE_JQL"
        const val AUTOUPDATE_TIMEOUT = "AUTOUPDATE_TIMEOUT"
        const val LAST_UPDATE = "LAST_UPDATE"
        const val TICKET_LAST_UPDATE = "TICKET_LAST_UPDATE"
    }
}
