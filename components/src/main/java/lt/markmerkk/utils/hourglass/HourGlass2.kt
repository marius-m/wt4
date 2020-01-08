package lt.markmerkk.utils.hourglass

import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventClockChange
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Responsible for calculating time
 */
class HourGlass2(
        private val eventBus: WTEventBus,
        private val timeProvider: TimeProvider
) {

    private var clockStart: DateTime? = null

    val start: DateTime
        get() = clockStart ?: timeProvider.now()
    val end: DateTime
        get() = timeProvider.now()
    val duration: Duration
        get() {
            val now = timeProvider.now()
            val start: DateTime = clockStart ?: now
            return Duration(start, now)
        }

    fun startFrom(suggestStart: DateTime) {
        val now = timeProvider.now()
        if (suggestStart.isAfter(now) || suggestStart.isEqual(now)) {
            clockStart = now
        } else {
            clockStart = suggestStart
        }
        eventBus.post(EventClockChange(suggestStart, duration))
    }

    fun start() {
        clockStart = timeProvider.now()
        eventBus.post(EventClockChange(start, duration))
    }

    fun stop() {
        clockStart = null
        eventBus.post(EventClockChange(start, duration))
    }

    fun changeStart(suggestStart: DateTime) {
        val now = timeProvider.now()
        if (suggestStart.isAfter(now) || suggestStart.isEqual(now)) {
            clockStart = now
        } else {
            clockStart = suggestStart
        }
        eventBus.post(EventClockChange(start, duration))
    }

    fun isRunning(): Boolean = clockStart != null

}