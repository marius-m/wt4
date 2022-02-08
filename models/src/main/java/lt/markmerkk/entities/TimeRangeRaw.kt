package lt.markmerkk.entities

import lt.markmerkk.utils.LogFormatters
import org.joda.time.LocalDate
import org.joda.time.LocalTime

data class TimeRangeRaw(
    val startDateRaw: String,
    val startTimeRaw: String,
    val endDateRaw: String,
    val endTimeRaw: String
) {
    val startDate = LogFormatters.dateFromRawOrDefault(startDateRaw)
    val startTime = LogFormatters.timeFromRawOrDefault(startTimeRaw)
    val endDate = LogFormatters.dateFromRawOrDefault(endDateRaw)
    val endTime = LogFormatters.timeFromRawOrDefault(endTimeRaw)

    val start = startDate.toLocalDateTime(startTime)
    val end = endDate.toLocalDateTime(endTime)

    val dtStart = startDate.toDateTime(startTime)
    val dtEnd = endDate.toDateTime(endTime)

    companion object {
        fun TimeRangeRaw.withStartDate(startDate: LocalDate): TimeRangeRaw {
            return TimeRangeRaw(
                startDateRaw = LogFormatters.shortFormatDate.print(startDate),
                startTimeRaw = startTimeRaw,
                endDateRaw = endDateRaw,
                endTimeRaw = endTimeRaw
            )
        }

        fun TimeRangeRaw.withStartTime(startTime: LocalTime): TimeRangeRaw {
            return TimeRangeRaw(
                startDateRaw = startDateRaw,
                startTimeRaw = LogFormatters.shortFormat.print(startTime),
                endDateRaw = endDateRaw,
                endTimeRaw = endTimeRaw
            )
        }

        fun TimeRangeRaw.withEndDate(endDate: LocalDate): TimeRangeRaw {
            return TimeRangeRaw(
                startDateRaw = startDateRaw,
                startTimeRaw = startTimeRaw,
                endDateRaw = LogFormatters.shortFormatDate.print(endDate),
                endTimeRaw = endTimeRaw
            )
        }

        fun TimeRangeRaw.withEndTime(endTime: LocalTime): TimeRangeRaw {
            return TimeRangeRaw(
                startDateRaw = startDateRaw,
                startTimeRaw = startTimeRaw,
                endDateRaw = endDateRaw,
                endTimeRaw = LogFormatters.shortFormat.print(endTime)
            )
        }

    }
}