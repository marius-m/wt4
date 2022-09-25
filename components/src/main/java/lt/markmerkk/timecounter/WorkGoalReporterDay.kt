package lt.markmerkk.timecounter

import lt.markmerkk.entities.DateRange
import org.joda.time.DateTime
import org.joda.time.Duration

class WorkGoalReporterDay(
    private val reporter: WorkGoalReporter,
) : WorkGoalReporter.ReporterDecorator {
    override fun reportLogged(durationLogged: Duration): String {
        return reporter.reportLogged(durationLogged)
    }

    override fun reportLoggedOngoing(durationLogged: Duration, durationOngoing: Duration): String {
        return reporter.reportLoggedWithOngoing(durationLogged, durationOngoing)
    }

    override fun reportPace(
        now: DateTime,
        displayDateRange: DateRange,
        durationWorked: Duration,
    ): String {
        return reporter.reportPaceDay(now, displayDateRange, durationWorked)
    }

    override fun reportShouldComplete(now: DateTime, durationWorked: Duration): String {
        return reporter.reportDayShouldComplete(now, durationWorked)
    }

    override fun reportGoal(dtTarget: DateTime, durationWorked: Duration): String {
        return reporter.reportDayGoalDuration(dtTarget, durationWorked)
    }

    override fun reportSchedule(dtTarget: DateTime): String {
        return reporter.reportDaySchedule(dtTarget)
    }

    override fun reportSummary(
        now: DateTime,
        displayDateRange: DateRange,
        durationLogged: Duration,
        durationOngoing: Duration,
    ): String {
        val durationWorked = durationLogged
            .plus(durationOngoing)
        val reportTotal = if (durationOngoing == Duration.ZERO) {
            reportLogged(durationLogged)
        } else {
            reportLoggedOngoing(
                durationLogged = durationLogged,
                durationOngoing = durationOngoing,
            )
        }
        val reportPace = reportPace(now, displayDateRange, durationWorked)
        val reportGoal = reportGoal(
            displayDateRange.selectDate.toDateTimeAtStartOfDay(),
            durationWorked,
        )
        val reportSchedule = reportSchedule(
            displayDateRange.selectDate.toDateTimeAtStartOfDay(),
        )
        val reports = listOf(
            "${reportTotal}\n",
            reportPace,
            reportGoal,
            reportSchedule,
        )
        val reportsAsString = reports
            .filter { it.isNotEmpty() }
            .joinToString(separator = "\n")
        return reportsAsString
    }
}