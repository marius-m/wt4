package lt.markmerkk.timeselect.entities

import org.joda.time.LocalTime

data class TimeSelectResult(
    val timeSelectionOld: LocalTime,
    val timeSelectionNew: LocalTime,
    val extra: String
) {
    companion object {
        fun withNewValue(
            request: TimeSelectRequest,
            timeSelection: LocalTime
        ): TimeSelectResult {
            return TimeSelectResult(
                timeSelectionOld = request.timeSelection,
                timeSelectionNew = timeSelection,
                extra = request.extra
            )
        }
    }
}