package lt.markmerkk.utils

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

object DateSwitcherFormatterJoda {

    private val dayOfWeekFormatter = DateTimeFormat.forPattern("EE")
    private val shortFormatter = DateTimeFormat.forPattern("dd MMMM")

    @JvmStatic fun formatDateForDay(localDate: LocalDate): String {
        return "${shortFormatter.print(localDate)} (${dayOfWeekFormatter.print(localDate)})"
    }

    @JvmStatic fun formatDateForWeek(localDate: LocalDate): String {
        val startOfWeek = localDate.withDayOfWeek(1)
        val endOfWeek = localDate.withDayOfWeek(7)
        return "${shortFormatter.print(startOfWeek)} - ${shortFormatter.print(endOfWeek)}"
    }
}