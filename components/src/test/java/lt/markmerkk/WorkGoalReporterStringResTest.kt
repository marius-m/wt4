package lt.markmerkk

import lt.markmerkk.timecounter.WorkGoalReporter

class WorkGoalReporterStringResTest: WorkGoalReporter.StringRes {
    override fun resTotal(): String = "Total"
    override fun resPace(): String = "Pace"
    override fun resPaceDay(): String = "Day pace"
    override fun resPaceWeek(): String = "Week pace"
    override fun resDayGoal(): String = "Day goal"
    override fun resWeekGoal(): String = "Week goal"
    override fun resLeft(): String = "left"
    override fun resShouldComplete(): String = "Should complete"
    override fun resShouldCompleteDay(): String = "Should complete day at"
    override fun resShouldCompleteWeek(): String = "Should complete week at"
    override fun resDaySchedule(): String = "Day schedule"
    override fun resBreak(): String = "Break"
    override fun resComplete(): String = "Complete"
    override fun resTomorrow(): String = "Tomorrow"
}