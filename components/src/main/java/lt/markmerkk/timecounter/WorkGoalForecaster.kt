package lt.markmerkk.timecounter

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
class WorkGoalForecaster(
    val workDays: WorkDays = WorkDays.asDefault(),
) {
    fun forecastWeekDurationGoalForWholeDay(
        targetDate: LocalDate,
    ): Duration {
        val targetWeekDays = workDays
            .weekDayRulesInSequenceByDate(targetDate = targetDate)
        return targetWeekDays.duration()
    }

    fun forecastWeekDurationGoalForTargetTime(
        targetDate: LocalDate,
        targetTime: LocalTime,
    ): Duration {
        val targetWeekDayRules = workDays
            .weekDayRulesInSequenceByDate(targetDate = targetDate)
        val targetWeekDayRulesWithoutLast = targetWeekDayRules
            .dropLast(1)
        val targetWeekDayRuleLast = targetWeekDayRules
            .last()
        return targetWeekDayRulesWithoutLast.duration()
            .plus(targetWeekDayRuleLast.workDurationWithTargetEnd(targetTime))
    }

    fun forecastDayDurationGoalForWholeDay(
        targetDate: LocalDate,
    ): Duration {
        val weekDayRuleByDate = workDays.weekDayRulesByDate(targetDate)
        return weekDayRuleByDate.workDuration
    }

    fun forecastDayDurationGoalForTargetTime(
        targetDate: LocalDate,
        targetTime: LocalTime,
    ): Duration {
        val weekDayRuleByDate = workDays.weekDayRulesByDate(targetDate)
        return weekDayRuleByDate.workDurationWithTargetEnd(targetEndTime = targetTime)
    }
}