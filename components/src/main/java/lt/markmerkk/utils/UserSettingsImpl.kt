package lt.markmerkk.utils

import lt.markmerkk.*

/**
 * Controller for holding persistent data
 */
class UserSettingsImpl(
        private val isOauth: Boolean,
        private val settings: HashSettings
) : UserSettings {

    private var host: String = ""
    private var username: String = ""
    private var password: String = ""
    override var issueJql: String = Const.DEFAULT_JQL_USER_ISSUES
    override var version = -1
    override var autoUpdateMinutes: Int = -1
    override var lastUpdate: Long = -1
    override var ticketLastUpdate: Long = -1
    override var ticketStatusUpdate: Long = -1
    override var onlyCurrentUserIssues: Boolean = true
    override var ticketFilterIncludeAssignee: Boolean = true
    override var ticketFilterIncludeReporter: Boolean = true
    override var ticketFilterIncludeIsWatching: Boolean = true
    override var settingsAutoStartClock: Boolean = true
    override var settingsAutoSync: Boolean = true

    private var oauthHost: String = ""
    private var oauthPrivateKey: String = ""
    private var oauthConsumerKey: String = ""
    private var oauthTokenSecret: String = ""
    private var oauthAccessKey: String = ""
    private var jiraUserName: String = ""
    private var jiraUserEmail: String = ""
    private var jiraUserDisplayName: String = ""

    override fun onAttach() {
        settings.load()
        host = settings.get(HOST, "")
        username = settings.get(USER, "")
        password = settings.get(PASS, "")
        version = settings.getInt(VERSION, -1)
        autoUpdateMinutes = settings.getLong(AUTOUPDATE_TIMEOUT, -1).toInt()
        lastUpdate = settings.getLong(LAST_UPDATE, -1)
        issueJql = settings.get(ISSUE_JQL, Const.DEFAULT_JQL_USER_ISSUES)
        ticketLastUpdate = settings.getLong(TICKET_LAST_UPDATE, -1)
        ticketStatusUpdate = settings.getLong(TICKET_STATUS_UPDATE, -1)
        ticketFilterIncludeAssignee = settings.getBoolean(TICKET_FILTER_INCLUDE_ASSIGNEE, true)
        ticketFilterIncludeReporter = settings.getBoolean(TICKET_FILTER_INCLUDE_REPORTER, true)
        ticketFilterIncludeIsWatching = settings.getBoolean(TICKET_FILTER_INCLUDE_IS_WATCHING, true)
        oauthHost = settings.get(OAUTH_HOST, "")
        oauthPrivateKey = settings.get(OAUTH_PRIVATE_KEY, "")
        oauthConsumerKey = settings.get(OAUTH_CONSUMER_KEY, "")
        oauthTokenSecret = settings.get(OAUTH_TOKEN_SECRET, "")
        oauthAccessKey = settings.get(OAUTH_ACCESS_KEY, "")
        jiraUserName = settings.get(JIRA_USER_NAME, "")
        jiraUserEmail = settings.get(JIRA_USER_EMAIL, "")
        jiraUserDisplayName = settings.get(JIRA_USER_DISPLAY_NAME, "")
        onlyCurrentUserIssues = settings.getBoolean(ISSUE_ONLY_CURRENT_USER_ISSUES, true)
        settingsAutoStartClock = settings.getBoolean(SETTINGS_AUTO_START_CLOCK, true)
        settingsAutoSync = settings.getBoolean(SETTINGS_AUTO_SYNC, true)
    }

    override fun onDetach() {
        settings.set(HOST, host)
        settings.set(USER, username)
        settings.set(PASS, password)
        settings.set(VERSION, version.toString())
        settings.set(ISSUE_JQL, issueJql)
        settings.set(AUTOUPDATE_TIMEOUT, autoUpdateMinutes.toString())
        settings.set(LAST_UPDATE, lastUpdate.toString())
        settings.set(TICKET_LAST_UPDATE, ticketLastUpdate.toString())
        settings.set(TICKET_STATUS_UPDATE, ticketStatusUpdate.toString())
        settings.set(TICKET_FILTER_INCLUDE_ASSIGNEE, ticketFilterIncludeAssignee.toString())
        settings.set(TICKET_FILTER_INCLUDE_REPORTER, ticketFilterIncludeReporter.toString())
        settings.set(TICKET_FILTER_INCLUDE_IS_WATCHING, ticketFilterIncludeIsWatching.toString())
        settings.set(OAUTH_HOST, oauthHost)
        settings.set(OAUTH_PRIVATE_KEY, oauthPrivateKey)
        settings.set(OAUTH_CONSUMER_KEY, oauthConsumerKey)
        settings.set(OAUTH_TOKEN_SECRET, oauthTokenSecret)
        settings.set(OAUTH_ACCESS_KEY, oauthAccessKey)
        settings.set(JIRA_USER_NAME, jiraUserName)
        settings.set(JIRA_USER_EMAIL, jiraUserEmail)
        settings.set(JIRA_USER_DISPLAY_NAME, jiraUserDisplayName)
        settings.set(JIRA_USER_ACCOUNT_ID, "")
        settings.set(ISSUE_ONLY_CURRENT_USER_ISSUES, onlyCurrentUserIssues.toString())
        settings.set(SETTINGS_AUTO_START_CLOCK, settingsAutoStartClock.toString())
        settings.set(SETTINGS_AUTO_SYNC, settingsAutoSync.toString())
        settings.save()
    }

    override fun jiraOAuthPreset(): JiraOAuthPreset = JiraOAuthPreset(oauthHost, oauthPrivateKey, oauthConsumerKey)
    override fun jiraOAuthCreds(): JiraOAuthCreds = JiraOAuthCreds(oauthTokenSecret, oauthAccessKey)
    override fun jiraUser(): JiraUser  = JiraUser(jiraUserName, jiraUserDisplayName, jiraUserEmail)
    override fun jiraBasicCreds(): JiraBasicCreds = JiraBasicCreds(host, username, password)

    override fun changeOAuthPreset(
            host: String,
            privateKey: String,
            consumerKey: String
    ) {
        this.oauthHost = host
        this.oauthPrivateKey = privateKey
        this.oauthConsumerKey = consumerKey
    }

    override fun changeOAuthCreds(tokenSecret: String, accessKey: String) {
        this.oauthTokenSecret = tokenSecret
        this.oauthAccessKey = accessKey
    }

    override fun changeJiraUser(
            name: String,
            email: String,
            displayName: String,
    ) {
        this.jiraUserName = name
        this.jiraUserEmail = email
        this.jiraUserDisplayName = displayName
    }

    override fun changeBasicCreds(hostname: String, username: String, password: String) {
        this.host = hostname
        this.username = username
        this.password = password
    }

    override fun resetUserData() {
        this.oauthTokenSecret = ""
        this.oauthAccessKey = ""
        this.jiraUserName = ""
        this.jiraUserDisplayName = ""
        this.jiraUserEmail = ""
    }

    companion object {
        const val HOST = "HOST"
        const val USER = "USER"
        const val PASS = "PASS"
        const val VERSION = "VERSION"
        const val ISSUE_JQL = "ISSUE_JQL"
        const val ISSUE_ONLY_CURRENT_USER_ISSUES = "ISSUE_ONLY_CURRENT_USER_ISSUES"
        const val AUTOUPDATE_TIMEOUT = "AUTOUPDATE_TIMEOUT"
        const val LAST_UPDATE = "LAST_UPDATE"
        const val TICKET_LAST_UPDATE = "TICKET_LAST_UPDATE"
        const val TICKET_STATUS_UPDATE = "TICKET_STATUS_UPDATE"
        const val TICKET_FILTER_INCLUDE_ASSIGNEE = "TICKET_FILTER_INCLUDE_ASSIGNEE"
        const val TICKET_FILTER_INCLUDE_REPORTER = "TICKET_FILTER_INCLUDE_REPORTER"
        const val TICKET_FILTER_INCLUDE_IS_WATCHING = "TICKET_FILTER_INCLUDE_IS_WATCHING"
        const val OAUTH_HOST = "OAUTH_HOST"
        const val OAUTH_PRIVATE_KEY = "OAUTH_PRIVATE_KEY"
        const val OAUTH_CONSUMER_KEY = "OAUTH_CONSUMER_KEY"
        const val OAUTH_TOKEN_SECRET = "OAUTH_TOKEN_SECRET"
        const val OAUTH_ACCESS_KEY = "OAUTH_ACCESS_KEY"
        const val JIRA_USER_NAME = "JIRA_USER_NAME"
        const val JIRA_USER_EMAIL = "JIRA_USER_EMAIL"
        const val JIRA_USER_DISPLAY_NAME = "JIRA_USER_DISPLAY_NAME"

        @Deprecated("Deprecated key, should not be used")
        const val JIRA_USER_ACCOUNT_ID = "JIRA_USER_ACCOUNT_ID"
        const val SETTINGS_AUTO_START_CLOCK = "SETTINGS_AUTO_START_CLOCK"
        const val SETTINGS_AUTO_SYNC = "SETTINGS_AUTO_SYNC"
    }
}
