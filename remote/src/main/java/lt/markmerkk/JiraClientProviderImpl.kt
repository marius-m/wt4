package lt.markmerkk

import lt.markmerkk.entities.JiraCreds
import lt.markmerkk.UserSettings
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
class JiraClientProviderImpl(
        private val userSettings: UserSettings
) : JiraClientProvider {
    override val username: String
        get() = userSettings.username

    var cacheCreds = JiraCreds()

    var jiraClient: JiraClient? = null

    override fun client(): JiraClient {
        if (userSettings.host.isNullOrEmpty()) throw IllegalStateException("empty hostname")
        if (userSettings.username.isNullOrEmpty()) throw IllegalStateException("empty username")
        if (userSettings.password.isNullOrEmpty()) throw IllegalStateException("empty password")

        if (jiraClient == null || !creditsMatchCache(oldCreds = cacheCreds)) {
            jiraClient = JiraClient(userSettings.host, BasicCredentials(userSettings.username, userSettings.password))
        }

        cacheCreds = JiraCreds(
                userSettings.host,
                userSettings.username,
                userSettings.password
        )

        return jiraClient!!
    }

    fun creditsMatchCache(oldCreds: JiraCreds): Boolean {
        return oldCreds == JiraCreds(userSettings.host, userSettings.username, userSettings.password)
    }

}