package lt.markmerkk.timecounter

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.LocalDate

/**
 * Responsible for calculating duration for worklogs
 */
class WorkGoalDurationCalculator(
    private val hourGlass: HourGlass,
    private val activeDisplayRepository: ActiveDisplayRepository,
) {
    fun durationWorked(
        displayDateStart: LocalDate,
        displayDateEnd: LocalDate,
    ): Duration {
        return durationRunningClock(
            displayDateStart = displayDateStart,
            displayDateEnd = displayDateEnd,
        ).plus(durationLoggedForTarget(displayDateStart))
    }

    fun durationRunningClock(
        hourGlass: HourGlass = this.hourGlass,
        displayDateStart: LocalDate,
        displayDateEnd: LocalDate,
    ): Duration {
        val intStart = displayDateStart.toDateTimeAtStartOfDay()
        val intEnd = displayDateEnd.toDateTimeAtStartOfDay()
        val isClockDateBetweenDisplayDate = Interval(
            intStart,
            intEnd
        ).contains(hourGlass.start)
            || hourGlass.start.toLocalDate().isEqual(displayDateStart)
        if (isClockDateBetweenDisplayDate) {
            return hourGlass.duration
        }
        return Duration.ZERO
    }

    fun durationLogged(): Duration {
        return activeDisplayRepository.totalAsDuration()
    }

    fun durationLoggedForTarget(target: LocalDate): Duration {
        return activeDisplayRepository
            .durationForTargetDate(target)
    }
}