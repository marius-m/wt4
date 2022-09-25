package lt.markmerkk.timecounter

import lt.markmerkk.entities.DateRange
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.LocalDate

class WorkGoalReporterWeek(
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
        return reporter.reportPaceWeek(now, displayDateRange, durationWorked)
    }

    override fun reportShouldComplete(now: DateTime, durationWorked: Duration): String {
        return reporter.reportWeekShouldComplete(now, durationWorked)
    }

    override fun reportGoal(now: DateTime, durationWorked: Duration): String {
        return reporter.reportWeekGoalDuration(now, durationWorked)
    }

    override fun reportSchedule(now: DateTime): String {
        return ""
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
        val reports = listOf(
            "${reportTotal}\n",
            reportPace,
        )
        val reportsAsString = reports
            .filter { it.isNotEmpty() }
            .joinToString(separator = "\n")
        return reportsAsString
    }
}