package lt.markmerkk

import lt.markmerkk.entities.JiraCreds
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import org.slf4j.LoggerFactory

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
        return client(
                userSettings.host,
                userSettings.username,
                userSettings.password
        )
    }

    override fun client(
            hostname: String,
            username: String,
            password: String
    ): JiraClient {
        if (hostname.isNullOrEmpty()) throw IllegalArgumentException("empty hostname")
        if (username.isNullOrEmpty()) throw IllegalArgumentException("empty username")
        if (password.isNullOrEmpty()) throw IllegalArgumentException("empty password")

        if (jiraClient == null || !creditsMatchCache(oldCreds = cacheCreds)) {
            logger.info("[INFO] Creating a new JIRA client")
            jiraClient = JiraClient(
                    hostname,
                    BasicCredentials(
                            username,
                            password
                    )
            )
        } else {
            logger.info("[INFO] Reusing old JIRA client")
        }

        cacheCreds = JiraCreds(
                hostname,
                username,
                password
        )

        return jiraClient!!
    }

    fun creditsMatchCache(oldCreds: JiraCreds): Boolean {
        return oldCreds == JiraCreds(userSettings.host, userSettings.username, userSettings.password)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JiraClientProviderImpl::class.java)!!
    }

}