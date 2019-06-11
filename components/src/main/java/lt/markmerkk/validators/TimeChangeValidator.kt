package lt.markmerkk.validators

import lt.markmerkk.entities.TimeGap
import org.joda.time.DateTime

/**
 * Responsible for modifying time in correct time gaps
 */
object TimeChangeValidator {

    /**
     * Appends minutes to the end of the time gap
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
     */
    fun shrinkFromEnd(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        val newEnd = timeGap.end.minusMinutes(minutes)
        if (newEnd.isBefore(timeGap.start)) {
            return TimeGap.from(
                    start = newEnd,
                    end = newEnd
            )
        }
        return TimeGap.from(
                start = timeGap.start,
                end = newEnd
        )
    }

    /**
     * Adds more minutes to start of the time gap
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
     */
    fun shrinkFromStart(
            timeGap: TimeGap,
            minutes: Int
    ): TimeGap {
        val newStart = timeGap.start.plusMinutes(minutes)
        if (newStart.isAfter(timeGap.end)) {
            return TimeGap.from(
                    start = newStart,
                    end = newStart
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

    /**
     * Changes start of the gap
     */
    fun changeStart(
            start: DateTime,
            end: DateTime
    ): TimeGap {
        return TimeGap.from(start, end)
    }

    /**
     * Changes end of the gap
     */
    fun changeEnd(
            start: DateTime,
            end: DateTime
    ): TimeGap {
        if (end.isBefore(start)) {
            return TimeGap.from(
                    start = end,
                    end = end
            )
        }
        return TimeGap.from(start, end)
    }

}
