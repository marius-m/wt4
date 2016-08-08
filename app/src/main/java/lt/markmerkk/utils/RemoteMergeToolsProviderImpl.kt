package lt.markmerkk.utils

import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.*
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
class RemoteMergeToolsProviderImpl(
        private val remoteMergeClient: RemoteMergeClient,
        private val remoteMergeExecutor: RemoteMergeExecutor
) : RemoteMergeToolsProvider {
    override fun pushMerger(
            localLog: SimpleLog,
            filter: JiraFilter<SimpleLog>
    ): RemoteLogPush {
        return RemoteLogPushImpl(
                remoteMergeClient = remoteMergeClient,
                remoteMergeExecutor = remoteMergeExecutor,
                localLog = localLog,
                uploadValidator = filter
        )
    }

    override fun pullMerger(
            remoteLog: JiraWork,
            jiraLogFilter: JiraFilter<WorkLog>
    ): RemoteLogPullImpl {
        return RemoteLogPullImpl(
                mergeExecutor = remoteMergeExecutor,
                remoteLogFilter = jiraLogFilter,
                remoteIssue = remoteLog
        )
    }
}