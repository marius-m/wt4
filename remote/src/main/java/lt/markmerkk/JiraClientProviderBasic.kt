package lt.markmerkk

import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.RestClient
import net.rcarz.jiraclient.RestClientDefault
import org.slf4j.LoggerFactory

class JiraClientProviderBasic(
        private val userSettings: UserSettings
): JiraClientProvider {

    private var client: Client = Client.asEmpty()

    @Throws(AuthException::class)
    override fun newClient(): JiraClient {
        client = Client.asEmpty()
        return client()
    }

    override fun markAsError() {
        client.markHasError()
    }

    @Throws(AuthException::class)
    override fun client(): JiraClient {
        if (client.hasError()) {
            throw AuthException(
                    IllegalArgumentException(
                            "Client marked has error. " +
                                    "Please create new client to resume using JiraClient"
                    )
            )
        }
        val jiraBasicCreds = userSettings.jiraBasicCreds()
        val newClient = Client.new(
                jiraBasicCreds.host,
                jiraBasicCreds.username,
                jiraBasicCreds.password
        )
        if (newClient.host.isEmpty()
                || newClient.user.isEmpty()
                || newClient.pass.isEmpty()) {
            throw AuthException(IllegalStateException("Invalid credentials"))
        }
        val hasSameCredentials = client == newClient
        return if (!hasSameCredentials) {
            logger.debug("Creating new JIRA client for user ${newClient.user}")
            this.client = newClient
            return client.jiraClient()
        } else {
            this.client.jiraClient()
        }
    }

    override fun hostname(): String {
        return userSettings.jiraBasicCreds().host
    }

    override fun username(): String {
        return userSettings.jiraBasicCreds().username
    }

    private data class Client(
            val host: String,
            val user: String,
            val pass: String
    ) {

        @Transient private var jiraClient: JiraClient? = null
        @Transient private var isError: Boolean = false

        fun markHasError() {
            this.isError = true
        }

        fun hasError() = isError

        fun jiraClient(): JiraClient {
            if (jiraClient == null) {
                jiraClient = JiraClient.createBasicClient(
                        host,
                        BasicCredentials(
                                user,
                                pass
                        )
                )
            }
            return jiraClient!!
        }

        companion object {
            fun new(
                    host: String,
                    user: String,
                    pass: String
            ): Client {
                return Client(
                        host = host,
                        user = user,
                        pass = pass
                )
            }
            fun asEmpty(): Client {
                return Client(
                        host = "",
                        user = "",
                        pass = ""
                )
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}