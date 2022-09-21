package lt.markmerkk.entities

import lt.markmerkk.round
import lt.markmerkk.utils.LogFormatters
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.joda.time.Period

data class LocalTimeGap private constructor(
        val start: LocalTime,
        val end: LocalTime
) {

    val period: Period = Period(start, end)
    val duration: Duration = period.toStandardDuration()

    fun isOverlapping(otherTimeGap: LocalTimeGap): Boolean {
        return start.isBefore(otherTimeGap.end) && end.isAfter(otherTimeGap.start)
    }

    fun isOverlappingWithAny(otherTimeGaps: List<LocalTimeGap>): Boolean {
        return otherTimeGaps.any { it.isOverlapping(this) }
    }

    fun toStringShort(): String {
        return "%s - %s".format(
            LogFormatters.formatTime.print(start),
            LogFormatters.formatTime.print(end),
        )
    }

    companion object {
        val DEFAULT_BREAK_START = LocalTime.MIDNIGHT.plusHours(12)
        val DEFAULT_BREAK_END = LocalTime.MIDNIGHT.plusHours(13)
        fun asEmpty(): LocalTimeGap {
            return LocalTimeGap(start = LocalTime.MIDNIGHT, end = LocalTime.MIDNIGHT)
        }

        fun asDefaultBreak(): LocalTimeGap {
            return LocalTimeGap(
                start = DEFAULT_BREAK_START,
                end = DEFAULT_BREAK_END,
            )
        }

        /**
         * Ensures the time gap is a valid one
         */
        fun from(start: LocalTime, end: LocalTime): LocalTimeGap {
            val rStart = start.round()
            val rEnd = end.round()
            if (rStart.isEqual(rEnd)
                    || rEnd.isBefore(rStart)) {
                return LocalTimeGap(rStart, rStart)
            }
            return LocalTimeGap(rStart, rEnd)
        }
    }
}

fun List<LocalTimeGap>.hasOverlapping(): Boolean {
    val scannedTimeGaps = mutableListOf<LocalTimeGap>()
    val timeGapWithOverlap = mutableListOf<LocalTimeGap>()
    this.forEach { timeGap ->
        val hasOverlap = timeGap.isOverlappingWithAny(scannedTimeGaps)
        if (hasOverlap) {
            timeGapWithOverlap.add(timeGap)
        }
        scannedTimeGaps.add(timeGap)
    }
    return timeGapWithOverlap.isNotEmpty()
}

fun List<LocalTimeGap>.ignoreOverlapping(): List<LocalTimeGap> {
    val scannedTimeGaps = mutableListOf<LocalTimeGap>()
    val timeGapWithOverlap = mutableListOf<LocalTimeGap>()
    this.forEach { timeGap ->
        val hasOverlap = timeGap.isOverlappingWithAny(scannedTimeGaps)
        if (hasOverlap) {
            timeGapWithOverlap.add(timeGap)
        }
        scannedTimeGaps.add(timeGap)
    }
    return this.minus(timeGapWithOverlap)
}
