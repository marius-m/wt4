package lt.markmerkk.utils

import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraWork
import lt.markmerkk.merger.RemoteLogMerger
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
interface RemoteMergeToolsProvider {
    fun fetchMerger(remoteLog: JiraWork, jiraLogFilter: JiraFilter<WorkLog>): RemoteLogMerger
}