package lt.markmerkk

import lt.markmerkk.entities.TimeGap
import lt.markmerkk.utils.LogFormatters
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

/**
 * Can generate time range
 * Screens that provide time ranges
 */
class TimeGapGenerator(
    val startDateSource: Source,
    val startTimeSource: Source,
    val endDateSource: Source,
    val endTimeSource: Source,
) {

    fun generateTimeGap(): TimeGap {
        return TimeGap.fromRaw(
            startDateRaw = startDateSource.rawInput(),
            startTimeRaw = startTimeSource.rawInput(),
            endDateRaw = endDateSource.rawInput(),
            endTimeRaw = endTimeSource.rawInput()
        )
    }

    fun timeValuesStart(): List<LocalTime> {
        val timeValues = mutableListOf<LocalTime>()
        val dtStart: LocalDateTime = LogFormatters.formatDate.parseLocalDate(startDateSource.rawInput())
            .toLocalDateTime(LocalTime.MIDNIGHT)
        val dtEnd: LocalDateTime = dtStart
            .toLocalDate()
            .toLocalDateTime(LogFormatters.formatTime.parseLocalTime(endTimeSource.rawInput()))
        var currentTime: LocalDateTime = dtStart
        while (currentTime.isBefore(dtEnd) || currentTime == dtEnd) {
            timeValues.add(currentTime.toLocalTime())
            currentTime = currentTime.plusMinutes(1)
        }
        return timeValues.toList()
    }

    fun timeValuesEnd(): List<LocalTime> {
        val timeValues = mutableListOf<LocalTime>()
        val dtStart: LocalDateTime = LogFormatters.formatDate.parseLocalDate(startDateSource.rawInput())
            .toLocalDateTime(LogFormatters.formatTime.parseLocalTime(startTimeSource.rawInput()))
        val dtEnd: LocalDateTime = dtStart
            .toLocalDate()
            .plusDays(1)
            .toLocalDateTime(LocalTime.MIDNIGHT)
        var currentTime: LocalDateTime = dtStart
        while (currentTime.isBefore(dtEnd)) {
            timeValues.add(currentTime.toLocalTime())
            currentTime = currentTime.plusMinutes(1)
        }
        return timeValues.toList()
    }

    interface Source {
        fun rawInput(): String
    }
}
