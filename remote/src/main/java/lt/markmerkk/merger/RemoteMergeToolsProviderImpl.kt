package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
class RemoteMergeToolsProviderImpl(
        private val remoteMergeClient: RemoteMergeClient,
        private val remoteLogMergeExecutor: RemoteMergeExecutor<SimpleLog, WorkLog>,
        private val remoteIssueMergeExecutor: RemoteMergeExecutor<LocalIssue, Issue>
) : RemoteMergeToolsProvider {

    override fun logPushMerger(
            localLog: SimpleLog,
            filter: JiraFilter<SimpleLog>
    ): RemoteLogPush {
        return RemoteLogPushImpl(
                remoteMergeClient = remoteMergeClient,
                remoteMergeExecutor = remoteLogMergeExecutor,
                localLog = localLog,
                uploadValidator = filter
        )
    }

    override fun logPullMerger(
            remoteLog: JiraWork,
            filter: JiraFilter<WorkLog>
    ): RemoteLogPullImpl {
        return RemoteLogPullImpl(
                mergeExecutor = remoteLogMergeExecutor,
                remoteLogFilter = filter,
                remoteIssue = remoteLog
        )
    }

    override fun issuePullMerger(
            downloadMillis: Long,
            issue: Issue,
            filter: JiraFilter<Issue>
    ): RemoteIssuePull {
        return RemoteIssuePullImpl(
                remoteMergeExecutor = remoteIssueMergeExecutor,
                filter = filter,
                downloadMillis = downloadMillis,
                issue = issue
        )
    }

}