package lt.markmerkk.interactors

import lt.markmerkk.UserSettings
import lt.markmerkk.utils.LogUtils
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-08-13
 */
class AutoUpdateInteractorImpl(
        private val userSettings: UserSettings
) : AutoUpdateInteractor {

    override fun notifyUpdateComplete(lastUpdateMillis: Long) {
        userSettings.lastUpdate = lastUpdateMillis
    }

    override fun isAutoUpdateTimeoutHit(now: Long): Boolean {
        logger.debug("Check if update is necessary...")
        if (userSettings.autoUpdateMinutes == -1) {
            logger.debug("Auto update disabled")
            return false
        }
        if (userSettings.lastUpdate < 0) {
            logger.debug("Update was never done, executing...")
            return true
        }

        val timeout = userSettings.autoUpdateMinutes * 1000 * 60
        val updateGap = now - userSettings.lastUpdate
        logger.debug("Timeout set to ${userSettings.autoUpdateMinutes}")
        logger.debug("Time gap of last update ${LogUtils.formatDuration(updateGap)}")
        return updateGap >= timeout
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AutoUpdateInteractorImpl::class.java)!!
    }

}