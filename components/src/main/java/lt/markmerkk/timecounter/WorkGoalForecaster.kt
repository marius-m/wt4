package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import lt.markmerkk.utils.toStringShort
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.slf4j.LoggerFactory

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
    /**
     * Returns duration how much time you should have worked for the whole week in sequence
     */
    fun forecastWeekDurationShouldWorkedForWholeDay(
        targetDate: LocalDate,
    ): Duration {
        val targetWeekDays = workDays
            .workDayRulesInSequenceByDate(targetDate = targetDate)
        return targetWeekDays.duration()
    }

    /**
     * Returns duration how much time you should have worked in perfect conditions for [targetTime]
     * Considers whole week in a sequence
     */
    fun forecastWeekDurationShouldWorkedForTargetTime(
        targetDate: LocalDate,
        targetTime: LocalTime,
    ): Duration {
        val targetWeekDayRules = workDays
            .workDayRulesInSequenceByDate(targetDate = targetDate)
        val targetWeekDayRulesWithoutLast = targetWeekDayRules
            .dropLast(1)
        val targetWeekDayRuleLast = targetWeekDayRules
            .last()
        return targetWeekDayRulesWithoutLast.duration()
            .plus(targetWeekDayRuleLast.workDurationWithTargetEnd(targetTime))
    }

    /**
     * Returns duration how much time you should have worked for the concrete whole day
     */
    fun forecastDayDurationShouldWorkedForWholeDay(
        targetDate: LocalDate,
    ): Duration {
        val weekDayRuleByDate = workDays.workDayRulesByDate(targetDate)
        return weekDayRuleByDate.workDuration
    }

    /**
     * Returns duration how much time you should have worked in perfect conditions for [targetTime]
     */
    fun forecastDayDurationShouldWorkedForTargetTime(
        targetDate: LocalDate,
        targetTime: LocalTime,
    ): Duration {
        val weekDayRuleByDate = workDays.workDayRulesByDate(targetDate)
        return weekDayRuleByDate.workDurationWithTargetEnd(targetEndTime = targetTime)
    }

    /**
     * Returns how much time is left to work to finish the day
     */
    fun forecastShouldFinishDay(
        dtCurrent: DateTime,
        durationWorked: Duration,
    ): DateTime {
        val workDayRuleByDate = workDays.workDayRulesByDate(dtCurrent.toLocalDate())
        val dtWorkDayStart = dtCurrent.withTime(workDayRuleByDate.workSchedule.start)
        val timeGapCurrent = LocalTimeGap.from(
            start = workDayRuleByDate.workSchedule.start,
            end = dtCurrent.toLocalTime(),
        )
        val durationShouldHaveWorked = workDayRuleByDate
            .workDurationWithTargetEnd(targetEndTime = dtCurrent.toLocalTime())
        val durationTotalWorkLeft = workDayRuleByDate.workDuration
            .minus(durationShouldHaveWorked)
        val durationTotalWork = durationShouldHaveWorked.plus(durationTotalWorkLeft)
        val durationWorkedOffset = durationShouldHaveWorked.minus(durationWorked)
        val durationBreak = workDayRuleByDate.timeBreak.breakDurationFromTimeGap(timeGapCurrent)
        val durationTotalWorkFromStart = durationTotalWork
            .plus(durationWorkedOffset)
            .plus(durationBreak)
        val dtWorkLeftFromStart = dtWorkDayStart.plus(durationTotalWorkFromStart)
        l.debug(
            "forecastShouldFinishDay(" +
                "dtCurrent: {}," +
                " durationShouldHaveWorked: {}," +
                " durationTotalWorkLeft: {}," +
                " durationTotalWork: {}," +
                " durationWorkedOffset: {}," +
                " durationBreak: {}," +
                " durationTotalWorkFromStart: {}," +
                " dtWorkLeftFromStart: {}," +
                ")",
            dtCurrent,
            durationShouldHaveWorked.toStringShort(),
            durationTotalWorkLeft.toStringShort(),
            durationTotalWork.toStringShort(),
            durationWorkedOffset.toStringShort(),
            durationBreak.toStringShort(),
            durationTotalWorkFromStart.toStringShort(),
            dtWorkLeftFromStart,
        )
        return dtWorkLeftFromStart
    }

    fun dayGoal(dtCurrent: DateTime): Duration {
        val workDayRuleByDate = workDays.workDayRulesByDate(dtCurrent.toLocalDate())
        return workDayRuleByDate.workDuration
    }

    fun dayGoalLeft(
        dtCurrent: DateTime,
        durationWorked: Duration,
    ): Duration {
        val durationDayGoal = dayGoal(dtCurrent)
        val durationWorkLeft = durationDayGoal.minus(durationWorked)
        if (durationWorkLeft.isShorterThan(Duration.ZERO)) {
            return Duration.ZERO
        }
        return durationWorkLeft
    }

    fun daySchedule(dtCurrent: DateTime): WorkDayRule {
        return workDays.workDayRulesByDate(dtCurrent.toLocalDate())
    }

    companion object {
        private val l = LoggerFactory.getLogger(WorkGoalForecaster::class.java)!!
    }
}