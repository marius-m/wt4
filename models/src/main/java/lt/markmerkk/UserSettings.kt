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

    fun changeOAuthPreset(host: String, privateKey: String, consumerKey: String)
    fun changeOAuthCreds(tokenSecret: String, accessKey: String)
    fun changeOAuthUserCreds(name: String, email: String, displayName: String)
}

data class JiraOAuthPreset(
        val host: String,
        val privateKey: String,
        val consumerKey: String
) {
    val hostAsUri: URI = URI.create(host)
}

data class JiraOAuthCreds(val tokenSecret: String, val accessKey: String)
