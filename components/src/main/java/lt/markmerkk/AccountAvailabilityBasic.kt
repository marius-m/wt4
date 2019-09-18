package lt.markmerkk

import lt.markmerkk.utils.AccountAvailablility

class AccountAvailabilityBasic(
        private val userSettings: UserSettings,
        private val jiraClientProvider: JiraClientProvider
): AccountAvailablility {
    override fun host(): String = userSettings.jiraBasicCreds().host
    override fun isAccountReadyForSync(): Boolean {
        val jiraBasicCreds = userSettings.jiraBasicCreds()
        return !jiraBasicCreds.isEmpty() && !jiraClientProvider.hasError()
    }
}