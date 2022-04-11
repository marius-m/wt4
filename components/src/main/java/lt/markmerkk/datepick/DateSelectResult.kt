package lt.markmerkk.datepick

import org.joda.time.LocalDate

data class DateSelectResult(
    val dateSelectionOld: LocalDate,
    val dateSelectionNew: LocalDate,
    val extra: String
) {
    companion object {
        fun withNewValue(
            request: DateSelectRequest,
            timeSelection: LocalDate
        ): DateSelectResult {
            return DateSelectResult(
                dateSelectionOld = request.dateSelection,
                dateSelectionNew = timeSelection,
                extra = request.extra
            )
        }
    }
}