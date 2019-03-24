package lt.markmerkk.events

import lt.markmerkk.entities.Ticket

/**
 * Suggests a ticket to currently open screen
 */
class EventSuggestTicket(
        val ticket: Ticket
): EventsBusEvent