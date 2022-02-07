package lt.markmerkk

import lt.markmerkk.entities.TimeRangeRaw

/**
 * Can generate time range
 * Screens that provide time ranges
 */
interface TimeRangeGenerator {
    val startDateSource: Source
    val startTimeSource: Source
    val endDateSource: Source
    val endTimeSource: Source

    fun generateTimeRange(): TimeRangeRaw {
        return TimeRangeRaw(
            startDateRaw = startDateSource.rawInput(),
            startTimeRaw = startTimeSource.rawInput(),
            endDateRaw = endDateSource.rawInput(),
            endTimeRaw = endTimeSource.rawInput()
        )
    }

    interface Source {
        fun rawInput(): String
    }
}
