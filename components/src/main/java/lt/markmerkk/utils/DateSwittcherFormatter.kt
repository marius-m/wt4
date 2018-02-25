package lt.markmerkk.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateSwittcherFormatter {

    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EE")
    private val shortFormatter = DateTimeFormatter.ofPattern("dd MMMM")

    @JvmStatic fun formatDateForDay(localDate: LocalDate): String {
        return "${shortFormatter.format(localDate)} (${dayOfWeekFormatter.format(localDate)})"
    }

    @JvmStatic fun formatDateForWeek(localDate: LocalDate): String {
        val startOfWeek = localDate.with(DayOfWeek.MONDAY)
        val endOfWeek = localDate.with(DayOfWeek.SUNDAY)
        return "${shortFormatter.format(startOfWeek)} - ${shortFormatter.format(endOfWeek)}"
    }

}