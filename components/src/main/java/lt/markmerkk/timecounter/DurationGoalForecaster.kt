package lt.markmerkk.timecounter

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Forecasts duration of worktime needed to be logged for target date in a week
 *
 * @param workDayRules rule spec when day starts / ends (+ breaks)
 *
 * Ex. 1: for monday forecast would be ~8hrs (depending on breaks)
 * Ex. 2: for tuesday forecast would be 16hrs (depending on breaks)
 */
class DurationGoalForecaster(
    val workDays: WorkDays = WorkDays.asDefault(),
) {
    fun forecastDurationGoalForDay(
        targetDate: LocalDate,
    ): Duration {
        val targetWeekDays = workDays
            .spawnTargetDaysByDate(targetDate = targetDate)
        return targetWeekDays.duration()
    }

    fun forecastDurationGoalForTime(
        targetDate: LocalDate,
        targetTime: LocalTime,
    ): Duration {
        val targetWeekDays = workDays
            .spawnTargetDaysByDate(targetDate = targetDate)
        val targetWeekDaysWithoutLast = targetWeekDays
            .dropLast(1)
        val targetWeekDayLast = targetWeekDays
            .last()
        return targetWeekDays.duration()
    }
}