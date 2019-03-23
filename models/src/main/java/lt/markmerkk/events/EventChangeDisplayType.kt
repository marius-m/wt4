package lt.markmerkk.events

import lt.markmerkk.DisplayType

/**
 * Represents a change event that indicates a [DisplayType] change
 */
class EventChangeDisplayType(
        val displayType: DisplayType
): EventsBusEvent