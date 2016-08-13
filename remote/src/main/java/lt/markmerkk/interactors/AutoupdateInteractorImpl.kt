package lt.markmerkk.interactors

import lt.markmerkk.UserSettings

/**
 * @author mariusmerkevicius
 * @since 2016-08-13
 */
class AutoUpdateInteractorImpl(
        private val userSettings: UserSettings
) : AutoupdateInteractor {

    var lastUpdateMillis = -1L

    override fun notifyUpdateComplete(lastUpdateMillis: Long) {
        this.lastUpdateMillis = lastUpdateMillis
    }

    override fun isAutoUpdateTimeoutHit(now: Long): Boolean {
        if (userSettings.autoUpdateMinutes == -1) return false // no update
        if (lastUpdateMillis < 0) return true // never updated

        val timeout = userSettings.autoUpdateMinutes * 1000 * 60
        val updateGap = now - lastUpdateMillis
        return updateGap >= timeout
    }

}