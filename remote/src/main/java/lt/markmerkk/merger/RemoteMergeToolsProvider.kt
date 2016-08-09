package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.merger.RemoteLogPush
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
interface RemoteMergeToolsProvider {
    fun pullMerger(remoteLog: JiraWork, jiraLogFilter: JiraFilter<WorkLog>): RemoteLogPull
    fun pushMerger(localLog: SimpleLog, filter: JiraFilter<SimpleLog>): RemoteLogPush
}