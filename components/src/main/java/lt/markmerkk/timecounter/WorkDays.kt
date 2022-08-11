package lt.markmerkk.timecounter

import org.joda.time.LocalDate
import org.slf4j.LoggerFactory

class WorkDays private constructor(
    val workDayRules: List<WorkDayRule>,
) {
    /**
     * Spawns work days from MON to SUN, but only needed days
     */
    fun spawnTargetDaysByDate(targetDate: LocalDate): List<WorkDayRule> {
        val targetWeekDay = WeekDay.fromInt(targetDate.dayOfWeek)
        l.debug("spawnTargetDaysByDate(targetWeekDay: {})", targetWeekDay)
        val targetWorkDayRules = workDayRules
            .filter { weekDayRule -> weekDayRule.weekDay.compareTo(targetWeekDay) <= 0 }
        return targetWorkDayRules
    }

    companion object {
        private val l = LoggerFactory.getLogger(WorkDays::class.java)!!
        private fun hasAllWeekDays(workDays: List<WorkDayRule>): Boolean {
            val missingWeekDays = WeekDay.values().toMutableSet()
            workDays.forEach { workDayRule ->
                if (missingWeekDays.contains(workDayRule.weekDay)) {
                    missingWeekDays.remove(workDayRule.weekDay)
                }
            }
            l.warn("Week day is missing ({})", missingWeekDays)
            return missingWeekDays.isEmpty()
        }

        fun asDefault(): WorkDays {
            return WorkDays(
                workDayRules = WorkDayRule.defaultForWorkWeek(),
            )
        }

        fun withWorkDays(workDays: List<WorkDayRule>): WorkDays {
            if (!hasAllWeekDays(workDays)) {
                throw IllegalArgumentException("Does not have all the week days")
            }
            return WorkDays(workDayRules = workDays)
        }
    }
}