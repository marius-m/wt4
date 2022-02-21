package lt.markmerkk.timeselect.entities

import org.joda.time.LocalTime

data class TimeSelectRequest(
    val timeSelection: LocalTime,
    val extra: String
) {
    companion object {
        fun asDefault(): TimeSelectRequest {
            return TimeSelectRequest(
                timeSelection = LocalTime.MIDNIGHT,
                 extra = ""
            )
        }

        fun asTimeFrom(
            timeSelection: LocalTime = LocalTime.MIDNIGHT
        ): TimeSelectRequest {
            return TimeSelectRequest(
                timeSelection = timeSelection,
                extra = TimeSelectType.FROM.name
            )
        }

        fun asTimeTo(
            timeSelection: LocalTime = LocalTime.MIDNIGHT
        ): TimeSelectRequest {
            return TimeSelectRequest(
                timeSelection = timeSelection,
                extra = TimeSelectType.TO.name
            )
        }
    }
}