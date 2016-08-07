package lt.markmerkk.utils

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 * Day gap time provider
 */
interface DayProvider {
    fun startDay(): Long
    fun endDay(): Long
}