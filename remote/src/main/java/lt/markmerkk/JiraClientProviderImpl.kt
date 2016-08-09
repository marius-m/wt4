package lt.markmerkk

import lt.markmerkk.mvp.UserSettings
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
class JiraClientProviderImpl(
        private val userSettings: UserSettings
) : JiraClientProvider {
    override val username: String
        get() = userSettings.username

    var jiraClient: JiraClient? = null

    override fun client(): JiraClient {
        if (jiraClient == null) {
            if (userSettings.host.isNullOrEmpty()) throw IllegalStateException("empty hostname")
            if (userSettings.username.isNullOrEmpty()) throw IllegalStateException("empty username")
            if (userSettings.password.isNullOrEmpty()) throw IllegalStateException("empty password")
            jiraClient = JiraClient(userSettings.host, BasicCredentials(userSettings.username, userSettings.password))
        }
        return jiraClient!!
    }

    override fun reset() {
        jiraClient = null
    }

}