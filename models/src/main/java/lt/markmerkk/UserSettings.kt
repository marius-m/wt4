package lt.markmerkk

import java.net.URI

interface UserSettings {
    fun onAttach()
    fun onDetach()

    var issueJql: String
    var host: String
    var username: String
    var password: String
    var version: Int
    var autoUpdateMinutes: Int
    var lastUpdate: Long
    var ticketLastUpdate: Long

    fun jiraOAuthPreset(): JiraOAuthPreset
    fun jiraOAuthCreds(): JiraOAuthCreds
    fun jiraUser(): JiraUser

    fun changeOAuthPreset(host: String, privateKey: String, consumerKey: String)
    fun changeOAuthCreds(tokenSecret: String, accessKey: String)
    fun changeJiraUser(name: String, email: String, displayName: String)
    fun resetUserData()
}

data class JiraOAuthPreset(
        val host: String,
        val privateKey: String,
        val consumerKey: String
) {
    val hostAsUri: URI = URI.create(host)
}

data class JiraOAuthCreds(val tokenSecret: String, val accessKey: String) {
    fun isEmpty(): Boolean = tokenSecret.isEmpty() || accessKey.isEmpty()
}
data class JiraUser(
        val name: String,
        val displayName: String,
        val email: String
) {
    fun isEmpty(): Boolean = name.isEmpty() || displayName.isEmpty() || email.isEmpty()
}
