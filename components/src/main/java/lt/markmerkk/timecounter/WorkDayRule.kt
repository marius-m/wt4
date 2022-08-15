package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.slf4j.LoggerFactory

/**
 * Defines rules work day (when work starts, ends, its breaks)
 */
data class WorkDayRule(
    val weekDay: WeekDay,
    val workSchedule: LocalTimeGap,
    val timeBreak: TimeBreak,
) {
    val workDuration: Duration
        get() = workDurationWithTargetEnd(targetEndTime = workSchedule.end)

    fun workDurationWithTargetEnd(targetEndTime: LocalTime): Duration {
        val endTime = if (targetEndTime.isBefore(workSchedule.end)) {
            targetEndTime
        } else {
            workSchedule.end
        }
        val targetGap = LocalTimeGap.from(workSchedule.start, endTime)
        val durationStartEnd = targetGap.period.toStandardDuration()
        val breakDurationFromTimeGap = timeBreak.breakDurationFromTimeGap(timeWork = targetGap)
        val totalDuration = durationStartEnd.minus(breakDurationFromTimeGap)
        l.debug(
            "durationWithTargetEnd(durationStartEnd: {}, breakDuration: {})",
            durationStartEnd.toStandardMinutes(),
            breakDurationFromTimeGap.toStandardMinutes(),
        )
        if (totalDuration.isShorterThan(Duration.ZERO)) {
            return Duration.ZERO
        }
        return totalDuration
    }

    companion object {
        private val l = LoggerFactory.getLogger(WorkDayRule::class.java)!!
        val DEFAULT_WEEK_DAY = WeekDay.MON
        val DEFAULT_WORK_START = LocalTime.MIDNIGHT.plusHours(8)
        val DEFAULT_WORK_END = LocalTime.MIDNIGHT.plusHours(17)

        fun emptyWithWeekDay(weekDay: WeekDay): WorkDayRule {
            return WorkDayRule(
                weekDay = weekDay,
                workSchedule = LocalTimeGap.asEmpty(),
                timeBreak = TimeBreak.asEmpty(),
            )
        }

        fun default(): WorkDayRule {
            return WorkDayRule(
                weekDay = DEFAULT_WEEK_DAY,
                workSchedule = LocalTimeGap.from(DEFAULT_WORK_START, DEFAULT_WORK_END),
                timeBreak = TimeBreak.asDefault(),
            )
        }

        fun defaultWithWeekDay(weekDay: WeekDay): WorkDayRule {
            return WorkDayRule(
                weekDay = weekDay,
                workSchedule = LocalTimeGap.from(DEFAULT_WORK_START, DEFAULT_WORK_END),
                timeBreak = TimeBreak.asDefault(),
            )
        }

        fun defaultForWorkWeek(): List<WorkDayRule> {
            return listOf(
                defaultWithWeekDay(WeekDay.MON),
                defaultWithWeekDay(WeekDay.TUE),
                defaultWithWeekDay(WeekDay.WED),
                defaultWithWeekDay(WeekDay.THU),
                defaultWithWeekDay(WeekDay.FRI),
                emptyWithWeekDay(WeekDay.SAT),
                emptyWithWeekDay(WeekDay.SUN),
            )
        }
    }
}

fun List<WorkDayRule>.duration(): Duration {
    return this.fold(Duration.ZERO) { totalDuration, workDayRule -> totalDuration.plus(workDayRule.workDuration) }
}
