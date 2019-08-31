package lt.markmerkk

import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.JiraClient
import org.slf4j.LoggerFactory

// todo incomplete validations on correct client creation
class JiraClientProviderOauth(
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
        val newClient = Client.new(
                userSettings.jiraOAuthPreset.host,
                userSettings.jiraOAuthPreset.privateKey,
                userSettings.jiraOAuthPreset.consumerKey,
                userSettings.jiraOAuthCreds.tokenSecret,
                userSettings.jiraOAuthCreds.accessKey
        )
        // todo Missing valid client check
//        if (newClient.host.isEmpty()
//                || newClient.user.isEmpty()
//                || newClient.pass.isEmpty()) {
//            throw AuthException(IllegalStateException("Invalid credentials"))
//        }
        val hasSameCredentials = client == newClient
        return if (!hasSameCredentials) {
            logger.debug("Creating new JIRA client for ${newClient.uri}")
            this.client = newClient
            return client.jiraClient()
        } else {
            this.client.jiraClient()
        }
    }

    private data class Client(
            val uri: String,
            val privateKey: String,
            val consumerKey: String,
            val tokenSecret: String,
            val accessKey: String
    ) {

        @Transient private var jiraClient: JiraClient? = null
        @Transient private var isError: Boolean = false

        fun markHasError() {
            this.isError = true
        }

        fun hasError() = isError

        fun jiraClient(): JiraClient {
            if (jiraClient == null) {
                jiraClient = JiraClient.createOAuthClient(
                        uri,
                        privateKey,
                        consumerKey,
                        tokenSecret,
                        accessKey
                )
            }
            return jiraClient!!
        }

        companion object {
            fun new(
                    uri: String,
                    privateKey: String,
                    consumerKey: String,
                    tokenSecret: String,
                    accessKey: String
            ): Client {
                return Client(
                        uri,
                        privateKey,
                        consumerKey,
                        tokenSecret,
                        accessKey
                )
            }
            fun asEmpty(): Client {
                return Client(
                        uri = "",
                        privateKey = "",
                        consumerKey = "",
                        tokenSecret = "",
                        accessKey = ""
                )
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}