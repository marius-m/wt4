package lt.markmerkk.utils

import org.joda.time.DateTime

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 * Day gap time provider
 */
interface DayProvider {
    fun startDay(): Long
    fun endDay(): Long
}