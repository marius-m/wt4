package lt.markmerkk.events

import lt.markmerkk.Const

/**
 * Represents an event when log is selected (for ex.: in calendar or in a table)
 */
class EventLogSelection(
        val selectedLogId: Long = Const.NO_ID
): EventsBusEvent