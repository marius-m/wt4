package lt.markmerkk

import lt.markmerkk.entities.TimeGap

/**
 * Can generate time range
 * Screens that provide time ranges
 */
interface TimeGapGenerator {
    val startDateSource: Source
    val startTimeSource: Source
    val endDateSource: Source
    val endTimeSource: Source

    fun generateTimeGap(): TimeGap {
        return TimeGap.fromRaw(
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
