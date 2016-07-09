package lt.markmerkk.entities

import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
data class JiraWork(
        val issue: Issue?,
        val worklogs: List<WorkLog>?
) {
    fun valid(): Boolean {
        if (issue == null) return false
        if (worklogs == null) return false
        return true
    }
}