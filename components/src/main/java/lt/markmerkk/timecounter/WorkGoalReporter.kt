package lt.markmerkk.timecounter

import lt.markmerkk.entities.DateRange
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.toStringShort
import org.joda.time.DateTime
import org.joda.time.Duration

class WorkGoalReporter(
    private val workGoalForecaster: WorkGoalForecaster,
    private val stringRes: StringRes,
) {

    fun reportLogged(durationLogged: Duration): String {
        return "%s: %s".format(
            stringRes.resTotal(),
            LogFormatters.humanReadableDurationShort(durationLogged),
        )
    }

    fun reportLoggedWithOngoing(
        durationLogged: Duration,
        durationOngoing: Duration,
    ): String {
        val durationTotal = durationLogged.plus(durationOngoing)
        return "%s: %s + %s = %s"
            .format(
                stringRes.resTotal(),
                LogFormatters.humanReadableDurationShort(durationLogged),
                LogFormatters.humanReadableDurationShort(durationOngoing),
                LogFormatters.humanReadableDurationShort(durationTotal),
            )
    }

    /**
     * Reports pace for the day
     * Will only return pace of looking at current day
     */
    fun reportPaceDay(
        now: DateTime,
        displayDateRange: DateRange,
        durationWorked: Duration,
    ): String {
        if (!displayDateRange.contains(now.toLocalDate())) {
            return ""
        }
        val durationGoal = workGoalForecaster.forecastDayDurationShouldWorkedForTargetTime(
            targetDate = now.toLocalDate(),
            targetTime = now.toLocalTime(),
        )
        val compareDurationWorkedToGoal = durationWorked.compareTo(durationGoal)
        val reportPace: String = if (compareDurationWorkedToGoal == 1 || compareDurationWorkedToGoal == 0) {
            val resultDuration = durationWorked.minus(durationGoal)
            "+%s".format(LogFormatters.humanReadableDurationShort(resultDuration))
        } else {
            val resultDuration = durationGoal.minus(durationWorked)
            "-%s".format(LogFormatters.humanReadableDurationShort(resultDuration))
        }
        return "%s: %s"
            .format(
                stringRes.resPaceDay(),
                reportPace,
            )
    }

    fun reportPaceWeek(
        now: DateTime,
        displayDateRange: DateRange,
        durationWorked: Duration,
    ): String {
        if (!displayDateRange.contains(now.toLocalDate())) {
            return ""
        }
        val durationGoal = workGoalForecaster.forecastWeekDurationShouldWorkedForTargetTime(
            targetDate = now.toLocalDate(),
            targetTime = now.toLocalTime(),
        )
        val compareDurationWorkedToGoal = durationWorked.compareTo(durationGoal)
        val reportPace: String = if (compareDurationWorkedToGoal == 1 || compareDurationWorkedToGoal == 0) {
            val resultDuration = durationWorked.minus(durationGoal)
            "+%s".format(LogFormatters.humanReadableDurationShort(resultDuration))
        } else {
            val resultDuration = durationGoal.minus(durationWorked)
            "-%s".format(LogFormatters.humanReadableDurationShort(resultDuration))
        }
        return "%s: %s"
            .format(
                stringRes.resPaceWeek(),
                reportPace,
            )
    }

    fun reportDayShouldComplete(
        now: DateTime,
        durationWorked: Duration,
    ): String {
        val dtShouldFinish = workGoalForecaster.forecastShouldFinishDay(
            dtCurrent = now,
            durationWorked = durationWorked,
        )
        return "%s: %s".format(
            stringRes.resShouldComplete(),
            LogFormatters.formatTime(dtCurrent = now, dtTarget = dtShouldFinish),
        )
    }

    fun reportDayGoalDuration(
        dtTarget: DateTime,
        durationWorked: Duration,
    ): String {
        return "%s: %s (%s %s)".format(
            stringRes.resDayGoal(),
            workGoalForecaster.dayGoal(dtTarget).toStringShort(),
            workGoalForecaster.dayGoalLeft(dtTarget, durationWorked).toStringShort(),
            stringRes.resLeft(),
        )
    }

    fun reportWeekGoalDuration(
        dtTarget: DateTime,
        durationWorked: Duration,
    ): String {
        return "%s: %s (%s %s)".format(
            stringRes.resWeekGoal(),
            workGoalForecaster.weekGoal().toStringShort(),
            workGoalForecaster.weekGoalLeft(durationWorked).toStringShort(),
            stringRes.resLeft(),
        )
    }

    fun reportDaySchedule(
        dtTarget: DateTime,
    ): String {
        val workDayRule = workGoalForecaster.daySchedule(dtCurrent = dtTarget)
        return "%s: %s %s (%s: %s)".format(
            stringRes.resDaySchedule(),
            workDayRule.weekDay,
            workDayRule.workSchedule.toStringShort(),
            stringRes.resBreak(),
            workDayRule.timeBreak.timeBreak.toStringShort(),
        )
    }

    fun reportWeekShouldComplete(
        now: DateTime,
        durationWorked: Duration,
    ): String {
        val dtShouldFinish = workGoalForecaster.forecastShouldFinishWeek(
            dtCurrent = now,
            durationWorked = durationWorked,
        )
        return "%s: %s".format(
            stringRes.resShouldComplete(),
            LogFormatters.formatTime(dtCurrent = now, dtTarget = dtShouldFinish),
        )
    }

    // TODO: Work day details for current day

    interface StringRes {
        fun resTotal(): String
        fun resPace(): String
        fun resPaceDay(): String
        fun resPaceWeek(): String
        fun resDayGoal(): String
        fun resWeekGoal(): String
        fun resLeft(): String
        fun resShouldComplete(): String
        fun resDaySchedule(): String
        fun resBreak(): String
    }

    interface ReporterDecorator {
        fun reportLogged(durationLogged: Duration): String
        fun reportLoggedOngoing(
            durationLogged: Duration,
            durationOngoing: Duration,
        ): String
        fun reportPace(
            now: DateTime,
            displayDateRange: DateRange,
            durationWorked: Duration,
        ): String
        fun reportShouldComplete(
            now: DateTime,
            durationWorked: Duration,
        ): String
        fun reportGoal(
            dtTarget: DateTime,
            durationWorked: Duration,
        ): String
        fun reportSchedule(
            dtTarget: DateTime,
        ): String

        fun reportSummary(
            now: DateTime,
            displayDateRange: DateRange,
            durationWorkedDay: Duration,
            durationLogged: Duration,
            durationOngoing: Duration,
        ): String
    }

    companion object {
        fun createReporterDay(
            wgReporter: WorkGoalReporter,
        ): ReporterDecorator {
            return WorkGoalReporterDay(reporter = wgReporter)
        }

        fun createReporterWeek(
            wgReporter: WorkGoalReporter,
        ): ReporterDecorator {
            return WorkGoalReporterWeek(reporter = wgReporter)
        }
    }
}