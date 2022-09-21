package lt.markmerkk.utils.hourglass

import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventClockChange
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Responsible for calculating time
 */
class HourGlassImpl(
        private val eventBus: WTEventBus,
        private val timeProvider: TimeProvider
) : HourGlass {

    private var clockStart: DateTime? = null

    override val start: DateTime
        get() = clockStart ?: timeProvider.now()
    override val end: DateTime
        get() = timeProvider.now()
    override val duration: Duration
        get() {
            val now = timeProvider.now()
            val start: DateTime = clockStart ?: now
            return Duration(start, now)
        }

    override fun startFrom(suggestStart: DateTime) {
        val now = timeProvider.now()
        if (suggestStart.isAfter(now) || suggestStart.isEqual(now)) {
            clockStart = now
        } else {
            clockStart = suggestStart
        }
        eventBus.post(EventClockChange(suggestStart, duration))
    }

    override fun start() {
        clockStart = timeProvider.now()
        eventBus.post(EventClockChange(start, duration))
    }

    override fun stop() {
        clockStart = null
        eventBus.post(EventClockChange(start, duration))
    }

    override fun changeStart(suggestStart: DateTime) {
        val now = timeProvider.now()
        if (suggestStart.isAfter(now) || suggestStart.isEqual(now)) {
            clockStart = now
        } else {
            clockStart = suggestStart
        }
        eventBus.post(EventClockChange(start, duration))
    }

    override fun isRunning(): Boolean = clockStart != null

}