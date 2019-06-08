package lt.markmerkk.mvp

import org.joda.time.DateTime

/**
 * Responsible for modifying time in correct time gaps
 * Note: There should always be at least 1 min gap between
 */
object QuickTimeModifyValidator {

    /**
     * Appends minutes to the end of the time gap
     * Note: There should always be 1 min gap
     */
    fun expandToEnd(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        return TimeGap.from(
                start = timeGap.start,
                end = timeGap.end.plusMinutes(minutes)
        )
    }

    /**
     * Subtracts minutes from the end of the time gap
     * Note: There will always be 1 min gap
     */
    fun shrinkFromEnd(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        return TimeGap.from(
                start = timeGap.start,
                end = timeGap.end.minusMinutes(minutes)
        )
    }

    /**
     * Adds more minutes to start of the time gap
     * Note: There will always be a 1 min gap
     */
    fun expandToStart(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        return TimeGap.from(
                start = timeGap.start.minusMinutes(minutes),
                end = timeGap.end
        )
    }

    /**
     * Subtracts minutes from time gap
     * Note: There will always be a 1 min gap
     */
    fun shrinkFromStart(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        val newStart = timeGap.start.plusMinutes(minutes)
        if (newStart.isAfter(timeGap.end)) {
            return TimeGap.from(
                    start = timeGap.end.minusMinutes(1),
                    end = timeGap.end
            )
        }
        return TimeGap.from(
                start = newStart,
                end = timeGap.end
        )
    }

    /**
     * Moves time gap by provided minutes
     */
    fun moveForward(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        return TimeGap.from(
                start = timeGap.start.plusMinutes(minutes),
                end = timeGap.end.plusMinutes(minutes)
        )
    }

    /**
     * Moves time gap by providede minutes
     */
    fun moveBackward(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        return TimeGap.from(
                start = timeGap.start.minusMinutes(minutes),
                end = timeGap.end.minusMinutes(minutes)
        )
    }

}

data class TimeGap private constructor(
        val start: DateTime,
        val end: DateTime
) {
    companion object {
        /**
         * Ensures the time gap is a valid one
         * Note: Will always have at least 1 min gap
         */
        fun from(start: DateTime, end: DateTime): TimeGap {
            if (start.isEqual(end)
                    || end.isBefore(start)) {
                return TimeGap(start, start.plusMinutes(1))
            }
            return TimeGap(start, end)
        }
    }
}