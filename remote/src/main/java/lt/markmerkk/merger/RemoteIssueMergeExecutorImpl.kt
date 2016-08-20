package lt.markmerkk.merger

import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.LocalIssueBuilder
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.database.interfaces.DBIndexable
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.DeleteJob
import lt.markmerkk.entities.jobs.InsertJob
import lt.markmerkk.entities.jobs.QueryJob
import lt.markmerkk.entities.jobs.UpdateJob
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-07-10
 */
class RemoteIssueMergeExecutorImpl(
        val dbExecutor: IExecutor
) : RemoteMergeExecutor<LocalIssue, Issue> {

    override fun create(entry: LocalIssue) {
        dbExecutor.execute(InsertJob(LocalIssue::class.java, entry))
    }

    override fun update(entry: LocalIssue) {
        dbExecutor.execute(UpdateJob(LocalIssue::class.java, entry));
    }

    override fun localEntityFromRemote(remoteEntry: Issue): LocalIssue? {
        val remoteId = SimpleLogBuilder.parseUri(remoteEntry.self.toString())
        if (remoteId <= 0) return null
        val queryJob = QueryJob<LocalIssue>(LocalIssue::class.java, DBIndexable { "id = " + remoteId })
        dbExecutor.execute(queryJob)
        return queryJob.result()
    }

    override fun recreate(oldLocalEntry: LocalIssue, remoteEntry: Issue) {
        dbExecutor.execute(DeleteJob(LocalIssue::class.java, oldLocalEntry))
        dbExecutor.execute(
                InsertJob(
                        LocalIssue::class.java,
                        LocalIssueBuilder(oldLocalEntry, remoteEntry).build()
                )
        )
    }

    override fun markAsError(oldLocalEntry: LocalIssue, error: Throwable) {
        dbExecutor.execute(
                UpdateJob(
                        LocalIssue::class.java,
                        LocalIssueBuilder(oldLocalEntry).buildWithError(error.message)
                )
        )
    }

}