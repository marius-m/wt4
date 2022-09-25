package lt.markmerkk.timecounter

import lt.markmerkk.Strings

class WorkGoalReporterStringRes(
    private val strings: Strings,
) : WorkGoalReporter.StringRes {
    override fun resTotal(): String = strings.getString("workgoal_reporter_total")

    override fun resPace(): String = strings.getString("workgoal_reporter_pace")

    override fun resPaceDay(): String = strings.getString("workgoal_reporter_pace_day")

    override fun resPaceWeek(): String = strings.getString("workgoal_reporter_pace_week")

    override fun resDayGoal(): String = strings.getString("workgoal_reporter_day_goal")

    override fun resWeekGoal(): String = strings.getString("workgoal_reporter_week_goal")

    override fun resLeft(): String = strings.getString("workgoal_reporter_left")

    override fun resShouldComplete(): String = strings.getString("workgoal_reporter_should_complete")

    override fun resDaySchedule(): String = strings.getString("workgoal_reporter_day_schedule")

    override fun resBreak(): String = strings.getString("workgoal_reporter_day_break")
}