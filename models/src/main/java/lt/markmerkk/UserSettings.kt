package lt.markmerkk

import java.net.URI

interface UserSettings {
    fun onAttach()
    fun onDetach()

    var issueJql: String
    var onlyCurrentUserIssues: Boolean
    var version: Int
    var autoUpdateMinutes: Int
    var lastUpdate: Long
    var ticketLastUpdate: Long
    var ticketStatusUpdate: Long
    var ticketFilterIncludeAssignee: Boolean
    var ticketFilterIncludeReporter: Boolean
    var ticketFilterIncludeIsWatching: Boolean
    var settingsAutoStartClock: Boolean
    var settingsAutoSync: Boolean

    fun jiraOAuthPreset(): JiraOAuthPreset
    fun jiraOAuthCreds(): JiraOAuthCreds
    fun jiraUser(): JiraUser
    fun jiraBasicCreds(): JiraBasicCreds

    fun changeOAuthPreset(host: String, privateKey: String, consumerKey: String)
    fun changeOAuthCreds(tokenSecret: String, accessKey: String)
    fun changeJiraUser(name: String, email: String, displayName: String, accountId: String)
    fun changeBasicCreds(hostname: String, username: String, password: String)
    fun resetUserData()
}

data class JiraOAuthPreset(
        val host: String,
        val privateKey: String,
        val consumerKey: String
) {
    val hostAsUri: URI = URI.create(host)
    fun isEmpty(): Boolean = host.isEmpty() || privateKey.isEmpty() || consumerKey.isEmpty()
}

data class JiraOAuthCreds(val tokenSecret: String, val accessKey: String) {
    fun isEmpty(): Boolean = tokenSecret.isEmpty() || accessKey.isEmpty()
}

data class JiraUser(
        val name: String, // may be empty due to new JIRA API changes
        val displayName: String,
        val email: String,
        val accountId: String // may be empty
) {
    fun isEmpty(): Boolean = name.isEmpty()
            && displayName.isEmpty()
            && email.isEmpty()
            && accountId.isEmpty()

    /**
     * Provides user identifier based on available value
     */
    fun identifierAsString(): String {
        if (name.isNotEmpty()) return name
        if (accountId.isNotEmpty()) return accountId
        if (email.isNotEmpty()) return email
        return displayName
    }

    companion object {
        fun asEmpty() = JiraUser(
                name = "",
                displayName = "",
                email = "",
                accountId = ""
        )
    }

}

data class JiraBasicCreds(val host: String, val username: String, val password: String) {
    fun isEmpty(): Boolean = host.isEmpty() || username.isEmpty() || password.isEmpty()
}
