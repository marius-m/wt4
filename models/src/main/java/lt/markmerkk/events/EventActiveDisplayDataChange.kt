package lt.markmerkk.events

import lt.markmerkk.entities.Log

/**
 * Indicates of data changes in [lt.markmerkk.LogRepository]
 */
class EventActiveDisplayDataChange(
    val data: List<Log>
) : EventsBusEvent