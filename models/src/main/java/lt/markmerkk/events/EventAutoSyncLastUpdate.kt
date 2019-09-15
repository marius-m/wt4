package lt.markmerkk.events

import org.joda.time.Duration

/**
 * Schedules automatic sync
 */
class EventAutoSyncLastUpdate(
        val lastUpdateDuration: Duration
) : EventsBusEvent