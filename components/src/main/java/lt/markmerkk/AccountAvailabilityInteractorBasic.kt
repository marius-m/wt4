package lt.markmerkk

import lt.markmerkk.utils.AccountAvailablilityInteractor

class AccountAvailabilityInteractorBasic(
        private val userSettings: UserSettings
): AccountAvailablilityInteractor {
    override fun isAccountReadyForSync(): Boolean {
        val jiraBasicCreds = userSettings.jiraBasicCreds()
        return jiraBasicCreds.host.isNotEmpty()
                && jiraBasicCreds.username.isNotEmpty()
                && jiraBasicCreds.password.isNotEmpty()
    }
}