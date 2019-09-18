package lt.markmerkk

import lt.markmerkk.utils.AccountAvailablility

class AccountAvailabilityOAuth(
        private val userSettings: UserSettings,
        private val jiraClientProvider: JiraClientProvider
) : AccountAvailablility {
    override fun host(): String = userSettings.jiraOAuthPreset().host
    override fun isAccountReadyForSync(): Boolean {
        return !userSettings.jiraOAuthPreset().isEmpty()
                && !userSettings.jiraOAuthCreds().isEmpty()
                && !userSettings.jiraUser().isEmpty()
                && !jiraClientProvider.hasError()
    }
}