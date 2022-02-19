package lt.markmerkk.events

import lt.markmerkk.entities.Log

/**
 * Indicates of data changes in [lt.markmerkk.ActiveDisplayRepository]
 */
class EventActiveDisplayDataChange(
    val data: List<Log>
) : EventsBusEvent