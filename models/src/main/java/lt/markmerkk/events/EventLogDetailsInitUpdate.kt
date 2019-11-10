package lt.markmerkk.events

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.Ticket

/**
 * Event to initialize LogDetailsWidget
 */
class EventLogDetailsInitUpdate(
        val log: SimpleLog
): EventsBusEvent