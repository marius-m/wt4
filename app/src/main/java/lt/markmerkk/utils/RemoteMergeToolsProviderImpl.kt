package lt.markmerkk.utils

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.merger.RemoteLogMergerImpl
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
    ): RemoteLogMergerImpl {
        return RemoteLogMergerImpl(
                mergeExecutor = mergeExecutor,
                remoteLogFilter = jiraLogFilter,
                remoteIssue = remoteLog
        )
    }
}