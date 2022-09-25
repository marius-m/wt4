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
        return targetWeekDays.workDuration()
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
        return targetWeekDayRulesWithoutLast.workDuration()
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
     * When there is no more work left, work left == [dtCurrent]
     * @param dtCurrent current date time
     * @param durationWorked duration how much time has been logged
     */
    fun forecastShouldFinishDay(
        dtCurrent: DateTime,
        durationWorked: Duration,
    ): DateTime {
        val workDayRuleByDate = workDays.workDayRulesByDate(dtCurrent.toLocalDate())
        val dtWorkDayStart = dtCurrent.withTime(workDayRuleByDate.workSchedule.start)
        val durationBreak = workDayRuleByDate.timeBreak.breakDurationFromTimeGap(
            timeWork = LocalTimeGap.from(
                start = workDayRuleByDate.workSchedule.start,
                end = dtCurrent.toLocalTime(),
            )
        )
        val durationWorkedOffset = durationWorkedOffsetDay(dtCurrent, durationWorked)
        val durationTotalWorkFromStart = workDayRuleByDate.workDuration
            .plus(durationWorkedOffset)
            .plus(durationBreak)
        val dtWorkLeftFromStart = dtWorkDayStart.plus(durationTotalWorkFromStart)
        val dtShouldFinish = if (dtWorkLeftFromStart.isBefore(dtCurrent)) {
            dtCurrent
        } else {
            dtWorkLeftFromStart
        }
        l.debug(
            "forecastShouldFinishDay(" +
                "dtCurrent: {}," +
                " durationWorkedOffset: {}," +
                " durationBreak: {}," +
                " durationTotalWorkFromStart: {}," +
                " dtWorkLeftFromStart: {}," +
                " dtShouldFinish: {}," +
                ")",
            dtCurrent,
            durationWorkedOffset.toStringShort(),
            durationBreak.toStringShort(),
            durationTotalWorkFromStart.toStringShort(),
            dtWorkLeftFromStart,
            dtShouldFinish,
        )
        return dtShouldFinish
    }

    /**
     * Returns how much time is left to work to finish the week
     */
    fun forecastShouldFinishWeek(
        dtCurrent: DateTime,
        durationWorked: Duration,
    ): DateTime {
        val workDayRules = workDays.workDayRulesInSequenceByDate(dtCurrent.toLocalDate())
        val workDayRuleLast = workDayRules.last()
        val durationWeekTotalWork = workDayRules.workDuration()
        val durationDayTotalWork = workDayRuleLast.workDuration
        val durationWorkedOffset = durationWorkedOffsetWeek(dtCurrent, durationWorked)
        val durationDayBreak = workDayRuleLast.timeBreak.breakDurationFromTimeGap(
            timeWork = LocalTimeGap.from(
                start = workDayRuleLast.workSchedule.start,
                end = dtCurrent.toLocalTime(),
            )
        )
        val durationTotalWorkFromStart = durationDayTotalWork
            .plus(durationWorkedOffset)
            .plus(durationDayBreak)
        val dtWorkDayStart = dtCurrent.withTime(workDayRuleLast.workSchedule.start)
        val dtWorkLeftFromStart = dtWorkDayStart.plus(durationTotalWorkFromStart)
        val dtShouldFinish = if (dtWorkLeftFromStart.isBefore(dtCurrent)) {
            dtCurrent
        } else {
            dtWorkLeftFromStart
        }
        l.debug(
            "forecastShouldFinishDay(" +
                "dtCurrent: {}," +
                " durationTotalWork: {}," +
                " durationWorkedOffset: {}," +
                " durationBreak: {}," +
                " durationTotalWorkFromStart: {}," +
                " dtWorkLeftFromStart: {}," +
                " dtShouldFinish: {}," +
                ")",
            dtCurrent,
            durationWeekTotalWork.toStringShort(),
            durationWorkedOffset.toStringShort(),
            durationDayBreak.toStringShort(),
            durationTotalWorkFromStart.toStringShort(),
            dtWorkLeftFromStart,
            dtShouldFinish,
        )
        return dtShouldFinish
    }

    fun dayGoal(dtTarget: DateTime): Duration {
        val workDayRuleByDate = workDays.workDayRulesByDate(dtTarget.toLocalDate())
        return workDayRuleByDate.workDuration
    }

    fun dayGoalLeft(
        dtTarget: DateTime,
        durationWorked: Duration,
    ): Duration {
        val durationDayGoal = dayGoal(dtTarget)
        val durationWorkLeft = durationDayGoal.minus(durationWorked)
        if (durationWorkLeft.isShorterThan(Duration.ZERO)) {
            return Duration.ZERO
        }
        return durationWorkLeft
    }

    fun weekGoal(): Duration {
        return workDays.workDayRules.workDuration()
    }

    fun weekGoalLeft(
        durationWorked: Duration,
    ): Duration {
        val durationDayGoal = weekGoal()
        val durationWorkLeft = durationDayGoal.minus(durationWorked)
        if (durationWorkLeft.isShorterThan(Duration.ZERO)) {
            return Duration.ZERO
        }
        return durationWorkLeft
    }

    fun daySchedule(dtCurrent: DateTime): WorkDayRule {
        return workDays.workDayRulesByDate(dtCurrent.toLocalDate())
    }

    /**
     * Returns how much time has worked, based on 'how much should have worked'
     * Calculates for a day
     * May be either positive or negative
     * @param dtCurrent current date time
     * @param durationWorked duration how much time has been logged
     */
    private fun durationWorkedOffsetDay(
        dtCurrent: DateTime,
        durationWorked: Duration,
    ): Duration {
        val durationShouldHaveWorked = forecastDayDurationShouldWorkedForTargetTime(
            targetDate = dtCurrent.toLocalDate(),
            targetTime = dtCurrent.toLocalTime(),
        )
        return durationShouldHaveWorked.minus(durationWorked)
    }

    /**
     * Returns how much time has worked, based on 'how much should have worked'
     * Calculates for a week
     * May be either positive or negative
     * @param dtCurrent current date time
     * @param durationWorked duration how much time has been logged
     */
    private fun durationWorkedOffsetWeek(
        dtCurrent: DateTime,
        durationWorked: Duration,
    ): Duration {
        val durationShouldHaveWorked = forecastWeekDurationShouldWorkedForTargetTime(
            targetDate = dtCurrent.toLocalDate(),
            targetTime = dtCurrent.toLocalTime(),
        )
        return durationShouldHaveWorked.minus(durationWorked)
    }

    companion object {
        private val l = LoggerFactory.getLogger(WorkGoalForecaster::class.java)!!
    }
}