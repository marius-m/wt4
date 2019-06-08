package lt.markmerkk.mvp

import org.joda.time.DateTime

/**
 * Modifies time according to the rules
 */
@Deprecated("Plase use QuickTimeModifierValidator or remove this altogether")
interface TimeQuickModifier {
    /**
     * Subtracts time from startTime
     */
    fun subtractStartTime(
            start: DateTime,
            end: DateTime
    )

    /**
     * Appends time to startTime.
     * Should not append to startTime whenever endTime cannot be ahead.
     */
    fun appendStartTime(
            start: DateTime,
            end: DateTime
    )

    /**
     * Subtracts time from endTime
     * Should not subtract whenever startTime is before endTime
     */
    fun subtractEndTime(
            start: DateTime,
            end: DateTime
    )

    /**
     * Appends end time
     */
    fun appendEndTime(
            start: DateTime,
            end: DateTime
    )

    interface Listener {
        /**
         * Reports back with changed time
         */
        fun onTimeModified(
                startDateTime: DateTime,
                endDateTime: DateTime
        )
    }

}