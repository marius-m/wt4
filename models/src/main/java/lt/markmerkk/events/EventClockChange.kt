package lt.markmerkk.events

import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Report active clock change
 */
class EventClockChange(
        val start: DateTime,
        val duration: Duration
) : EventsBusEvent