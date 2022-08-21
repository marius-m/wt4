package lt.markmerkk.timecounter

import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.Duration

class WorkGoalReporter(
    private val workGoalForecaster: WorkGoalForecaster,
    private val stringRes: StringRes,
) {

    fun reportLoggedTotal(durationLogged: Duration): String {
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

    fun reportPaceDay(
        now: DateTime,
        durationWorked: Duration,
    ): String {
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
                stringRes.resPace(),
                reportPace,
            )
    }

    fun reportShouldComplete(
        now: DateTime,
        durationWorked: Duration,
    ): String {
        val dtShouldFinish = workGoalForecaster.forecastShouldFinishDay(
            dtCurrent = now,
            durationWorked = durationWorked,
        )
        return "%s: %s".format(
            stringRes.resDayGoal(),
            LogFormatters.formatTime.print(dtShouldFinish),
        )
    }

    interface StringRes {
        fun resTotal(): String
        fun resPace(): String
        fun resDayGoal(): String
        fun resShouldComplete(): String
    }
}