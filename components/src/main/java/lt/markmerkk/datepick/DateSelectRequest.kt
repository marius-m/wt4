package lt.markmerkk.datepick

import lt.markmerkk.utils.LogFormatters
import org.joda.time.LocalDate

data class DateSelectRequest(
    val dateSelection: LocalDate,
    val extra: String
) {
    companion object {
        fun asDefault(): DateSelectRequest {
            return DateSelectRequest(
                dateSelection = LogFormatters.defaultDate,
                extra = ""
            )
        }
    }
}