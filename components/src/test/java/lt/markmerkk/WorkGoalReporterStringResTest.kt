package lt.markmerkk

import lt.markmerkk.timecounter.WorkGoalReporter

class WorkGoalReporterStringResTest: WorkGoalReporter.StringRes {
    override fun resTotal(): String = "Total"
    override fun resPace(): String = "Pace"
    override fun resDayGoal(): String = "Day goal completes"
    override fun resShouldComplete(): String = "Should complete"
}