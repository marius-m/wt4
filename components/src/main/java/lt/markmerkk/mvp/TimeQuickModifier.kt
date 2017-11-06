package lt.markmerkk.mvp

import java.time.LocalDateTime

/**
 * Modifies time according to the rules
 */
interface TimeQuickModifier {
    /**
     * Subtracts time from startTime
     */
    fun subtractStartTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    )

    /**
     * Appends time to startTime.
     * Should not append to startTime whenever endTime cannot be ahead.
     */
    fun appendStartTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    )

    /**
     * Subtracts time from endTime
     * Should not subtract whenever startTime is before endTime
     */
    fun subtractEndTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    )

    /**
     * Appends end time
     */
    fun appendEndTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    )

    interface Listener {
        /**
         * Reports back with changed time
         */
        fun onTimeModified(
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime
        )
    }

}