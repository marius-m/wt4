package lt.markmerkk.utils.hourglass

import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Responsible for calculating time
 */
interface HourGlass {

    val start: DateTime
    val end: DateTime
    val duration: Duration

    fun startFrom(suggestStart: DateTime)
    fun start()
    fun stop()
    fun changeStart(suggestStart: DateTime)
    fun isRunning(): Boolean
}