package lt.markmerkk.utils

import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraWork
import lt.markmerkk.merger.RemoteLogPullImpl
import lt.markmerkk.merger.RemoteMergeExecutor
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
class RemoteMergeToolsProviderImpl(
        private val mergeExecutor: RemoteMergeExecutor
) : RemoteMergeToolsProvider {
    override fun fetchMerger(
            remoteLog: JiraWork,
            jiraLogFilter: JiraFilter<WorkLog>
    ): RemoteLogPullImpl {
        return RemoteLogPullImpl(
                mergeExecutor = mergeExecutor,
                remoteLogFilter = jiraLogFilter,
                remoteIssue = remoteLog
        )
    }
}