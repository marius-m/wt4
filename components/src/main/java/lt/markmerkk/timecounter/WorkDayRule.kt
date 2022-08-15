package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.joda.time.Period
import org.slf4j.LoggerFactory

/**
 * Defines rules work day (when work starts, ends, its breaks)
 */
data class WorkDayRule(
    val weekDay: WeekDay,
    val workSchedule: LocalTimeGap,
    val breaks: TimeBreaks,
) {
    val duration: Duration
        get() = durationWithTargetEnd(targetTime = workSchedule.end)

    fun totalBreakDuration(): Duration {
        return breaks.duration()
    }

    fun durationWithTargetEnd(targetTime: LocalTime): Duration {
        val durationStartEnd = Period(workSchedule.start, targetTime)
            .toStandardDuration()
        val totalDuration = durationStartEnd.minus(totalBreakDuration())
        l.debug(
            "durationWithTargetEnd(durationStartEnd: {}, breakDuration: {})",
            durationStartEnd.toStandardMinutes(),
            totalBreakDuration().toStandardMinutes(),
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
                breaks = TimeBreaks.asEmpty(),
            )
        }

        fun default(): WorkDayRule {
            return WorkDayRule(
                weekDay = DEFAULT_WEEK_DAY,
                workSchedule = LocalTimeGap.from(DEFAULT_WORK_START, DEFAULT_WORK_END),
                breaks = TimeBreaks.asDefault(),
            )
        }

        fun defaultWithWeekDay(weekDay: WeekDay): WorkDayRule {
            return WorkDayRule(
                weekDay = weekDay,
                workSchedule = LocalTimeGap.from(DEFAULT_WORK_START, DEFAULT_WORK_END),
                breaks = TimeBreaks.asDefault(),
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
    return this.fold(Duration.ZERO) { totalDuration, workDayRule -> totalDuration.plus(workDayRule.duration) }
}
