package lt.markmerkk.interactors

/**
 * @author mariusmerkevicius
 * @since 2016-08-13
 */
interface AutoUpdateInteractor {
    fun notifyUpdateComplete(lastUpdateMillis: Long)
    fun isAutoUpdateTimeoutHit(now: Long): Boolean
}