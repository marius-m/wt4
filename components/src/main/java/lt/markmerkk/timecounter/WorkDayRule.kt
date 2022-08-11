package lt.markmerkk.timecounter

import org.joda.time.Duration
import org.joda.time.LocalTime
import org.joda.time.Period

/**
 * Defines rules work day (when work starts, ends, its breaks)
 */
data class WorkDayRule(
    val weekDay: WeekDay,
    val workStart: LocalTime,
    val workEnd: LocalTime,
    val breakDuration: Duration = Duration.ZERO,
) {
    val duration: Duration
        get() {
            val durationStartEnd = Period(workStart, workEnd)
                .toStandardDuration()
            val totalDuration = durationStartEnd.minus(breakDuration)
            if (totalDuration.isShorterThan(Duration.ZERO)) {
                return Duration.ZERO
            }
            return totalDuration
        }

    companion object {
        val DEFAULT_WORK_START = LocalTime.MIDNIGHT.plusHours(8)
        val DEFAULT_WORK_END = LocalTime.MIDNIGHT.plusHours(17)
        val DEFAULT_DURATION_BREAK = Duration.standardHours(1)

        fun emptyWithWeekDay(weekDay: WeekDay): WorkDayRule {
            return WorkDayRule(
                weekDay = weekDay,
                workStart = LocalTime.MIDNIGHT,
                workEnd = LocalTime.MIDNIGHT,
                breakDuration = Duration.ZERO,
            )
        }

        fun defaultWithWeekDay(weekDay: WeekDay): WorkDayRule {
            return WorkDayRule(
                weekDay = weekDay,
                workStart = DEFAULT_WORK_START,
                workEnd = DEFAULT_WORK_END,
                breakDuration = DEFAULT_DURATION_BREAK,
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