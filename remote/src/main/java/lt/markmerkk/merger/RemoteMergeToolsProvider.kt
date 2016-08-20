package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.merger.RemoteLogPush
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
interface RemoteMergeToolsProvider {
    fun logPullMerger(remoteLog: JiraWork, filter: JiraFilter<WorkLog>): RemoteLogPull
    fun logPushMerger(localLog: SimpleLog, filter: JiraFilter<SimpleLog>): RemoteLogPush
    fun issuePullMerger(downloadMillis: Long, issue: Issue, filter: JiraFilter<Issue>): RemoteIssuePull
}