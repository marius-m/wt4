package lt.markmerkk.mvp

import org.joda.time.DateTime
import java.lang.UnsupportedOperationException

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

    fun expandToStart(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        return TimeGap.from(
                start = timeGap.start.minusMinutes(minutes),
                end = timeGap.end
        )
    }

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

    fun moveForwardMinutes(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        throw UnsupportedOperationException()
    }

    fun moveBackwardMinutes(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        throw UnsupportedOperationException()
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