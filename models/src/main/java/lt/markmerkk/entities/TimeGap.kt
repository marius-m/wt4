package lt.markmerkk.entities

import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime

data class TimeGap private constructor(
        val start: DateTime,
        val end: DateTime
) {

    val duration: Duration = Duration(start, end)

    companion object {
        fun fromRaw(
            startDateRaw: String,
            startTimeRaw: String,
            endDateRaw: String,
            endTimeRaw: String
        ): TimeGap {
            val startDate = LogFormatters.dateFromRawOrDefault(startDateRaw)
            val startTime = LogFormatters.timeFromRawOrDefault(startTimeRaw)
            val endDate = LogFormatters.dateFromRawOrDefault(endDateRaw)
            val endTime = LogFormatters.timeFromRawOrDefault(endTimeRaw)
            return from(
                start = startDate.toDateTime(startTime),
                end = endDate.toDateTime(endTime)
            )
        }

        /**
         * Ensures the time gap is a valid one
         * Note: Will always have at least 1 min gap
         */
        fun from(start: DateTime, end: DateTime): TimeGap {
            if (start.isEqual(end)
                    || end.isBefore(start)) {
                return TimeGap(start, start)
            }
            return TimeGap(start, end)
        }

        fun TimeGap.withStartDate(startDate: LocalDate): TimeGap {
            return from(
                start = start.withDate(startDate),
                end = end
            )
        }

        fun TimeGap.withStartTime(startTime: LocalTime): TimeGap {
            return from(
                start = start.withTime(startTime),
                end = end
            )
        }

        fun TimeGap.withEndDate(endDate: LocalDate): TimeGap {
            return from(
                start = start,
                end = end.withDate(endDate)
            )
        }

        fun TimeGap.withEndTime(endTime: LocalTime): TimeGap {
            return from(
                start = start,
                end = end.withTime(endTime)
            )
        }
    }
}