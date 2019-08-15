package lt.markmerkk.worklogs

import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog

data class IssueWorklogPair(
    val issue: Issue,
    val worklogs: List<WorkLog>
)