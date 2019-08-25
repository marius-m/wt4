package lt.markmerkk

import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import org.slf4j.LoggerFactory

class JiraClientProvider(
        private val userSettings: UserSettings
) {

    private var client: Client = Client.asEmpty()

    fun newClient(): JiraClient {
        client = Client.asEmpty()
        return client()
    }

    fun markAsError() {
        client.markHasError()
    }

    @Throws(AuthException::class)
    fun client(): JiraClient {
        if (client.hasError()) {
            throw AuthException(
                    IllegalArgumentException(
                            "Client marked has error. " +
                                    "Please create new client to resume using JiraClient"
                    )
            )
        }
        val newClient = Client.new(
                userSettings.host,
                userSettings.username,
                userSettings.password
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
                jiraClient = JiraClient(
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