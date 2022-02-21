package lt.markmerkk.entities

import lt.markmerkk.DisplayTypeLength
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

data class DateRange(
    val selectDate: LocalDate,
    val start: LocalDate,
    val end: LocalDate
) {

    /**
     * Date range +1 day to include next day start
     */
    val endAsNextDay: LocalDate = end.plusDays(1)

    val startAsMillis = start.toDateTimeAtStartOfDay().millis

    val endAsMillis: Long = end.toDateTimeAtStartOfDay().millis

    val endNextDayAsMillis: Long = endAsNextDay.toDateTimeAtStartOfDay().millis

    companion object {
        fun byDisplayType(
            displayType: DisplayTypeLength,
            localDate: LocalDate
        ): DateRange {
            return when (displayType) {
                DisplayTypeLength.DAY -> {
                    forActiveDay(localDate)
                }
                DisplayTypeLength.WEEK -> {
                    forActiveWeek(localDate)
                }
            }
        }

        fun forActiveDay(localDate: LocalDate): DateRange {
            return DateRange(
                selectDate = localDate,
                start = localDate,
                end = localDate
            )
        }

        fun forActiveWeek(localDate: LocalDate): DateRange {
            val weekStart = localDate.withDayOfWeek(DateTimeConstants.MONDAY)
            val weekEnd = localDate.withDayOfWeek(DateTimeConstants.SUNDAY)
            return DateRange(
                selectDate = localDate,
                start = weekStart,
                end = weekEnd
            )
        }
    }
}