package lt.markmerkk

import lt.markmerkk.entities.JiraCreds
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.TokenCredentials
import org.slf4j.LoggerFactory
import rx.Single

@Deprecated("Replace with new JiraClientPovider")
class JiraClientProvider(
        private val userSettings: UserSettings
) {

    private var cacheCreds = JiraCreds()
    private var jiraClient: JiraClient? = null

    fun invalidateClient() {
        jiraClient = null
    }

    @Deprecated("")
    fun clientStream(): Single<JiraClient> {
        return clientStream(
                hostname = userSettings.host,
                username = userSettings.username,
                password = userSettings.password
        )
    }

    @Deprecated("")
    fun clientStream(
            hostname: String,
            username: String,
            password: String
    ): Single<JiraClient> {
        return Single.defer {
            Single.just(
                    client(
                            hostname = hostname,
                            username = username,
                            password = password
                    )
            )
        }
    }

    @Throws(IllegalArgumentException::class)
    fun clientFromCache(): JiraClient {
        return client(userSettings.host, userSettings.username, userSettings.password)
    }

    @Throws(IllegalArgumentException::class)
    fun client(
            hostname: String,
            username: String,
            password: String
    ): JiraClient {
        if (hostname.isEmpty()) throw IllegalArgumentException("empty hostname")
        if (username.isEmpty()) throw IllegalArgumentException("empty username")
        if (password.isEmpty()) throw IllegalArgumentException("empty password")

        if (jiraClient == null || !creditsMatchCache(oldCreds = cacheCreds)) {
            logger.info("Creating a new JIRA client")
            jiraClient = JiraClient(
                    hostname,
                    BasicCredentials(
                            username,
                            password
                    )
            )
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
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}