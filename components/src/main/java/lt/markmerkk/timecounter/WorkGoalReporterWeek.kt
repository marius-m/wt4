package lt.markmerkk.timecounter

import lt.markmerkk.entities.DateRange
import org.joda.time.DateTime
import org.joda.time.Duration

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

    override fun reportShouldComplete(
        now: DateTime,
        displayDateRange: DateRange,
        durationWorked: Duration,
    ): String {
        return reporter.reportWeekShouldComplete(now, displayDateRange, durationWorked)
    }

    override fun reportGoal(dtTarget: DateTime, durationWorked: Duration): String {
        return reporter.reportWeekGoalDuration(dtTarget, durationWorked)
    }

    override fun reportSchedule(dtTarget: DateTime): String {
        return ""
    }

    override fun reportSummary(
        now: DateTime,
        displayDateRange: DateRange,
        durationWorkedDay: Duration,
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
        val reportsDay = if (displayDateRange.contains(now.toLocalDate())) {
            listOf(
                reporter.reportPaceDay(
                    now = now,
                    displayDateRange,
                    durationWorkedDay,
                ).appendBreakOnNotEmpty(),
                reporter.reportDayShouldComplete(
                    now = now,
                    displayDateRange = displayDateRange,
                    durationWorked = durationWorkedDay,
                ).appendBreakOnNotEmpty(),
                reporter.reportDayGoalDuration(
                    dtTarget = now,
                    durationWorkedDay,
                ).appendBreakOnNotEmpty(),
                "\n",
            )
        } else {
            emptyList()
        }
        val reportsWeek = listOf(
            reportPace(now, displayDateRange, durationWorked)
                .appendBreakOnNotEmpty(),
            // reportShouldComplete(
            //     now = now,
            //     displayDateRange = displayDateRange,
            //     durationWorked = durationWorked,
            // ).appendBreakOnNotEmpty(),
            reportGoal(
                displayDateRange.selectDate.toDateTimeAtStartOfDay(),
                durationWorked,
            ).appendBreakOnNotEmpty(),
        )
        val reports = listOf(
            "${reportTotal}\n",
            "\n",
        ).plus(reportsDay)
            .plus(reportsWeek)
        val reportsAsString = reports
            .filter { it.isNotEmpty() }
            .joinToString(separator = "")
        return reportsAsString
    }

    companion object {
        fun String.appendBreakOnNotEmpty(): String {
            return when {
                this.isEmpty() -> ""
                else -> "${this}\n"
            }
        }
    }
}