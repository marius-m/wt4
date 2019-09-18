package lt.markmerkk

import lt.markmerkk.utils.AccountAvailablilityInteractor

class AccountAvailabilityInteractorOAuth(
        private val userSettings: UserSettings
) : AccountAvailablilityInteractor {
    override fun isAccountReadyForSync(): Boolean {
        return !userSettings.jiraOAuthPreset().isEmpty()
                && !userSettings.jiraOAuthCreds().isEmpty()
                && !userSettings.jiraUser().isEmpty()
    }
}