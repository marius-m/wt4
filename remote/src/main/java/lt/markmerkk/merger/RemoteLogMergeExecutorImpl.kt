package lt.markmerkk.merger

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.database.interfaces.DBIndexable
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.DeleteJob
import lt.markmerkk.entities.jobs.InsertJob
import lt.markmerkk.entities.jobs.QueryJob
import lt.markmerkk.entities.jobs.UpdateJob
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-07-10
 */
class RemoteLogMergeExecutorImpl(
        val dbExecutor: IExecutor
) : RemoteMergeExecutor<SimpleLog, WorkLog> {

    override fun create(entry: SimpleLog) {
        dbExecutor.execute(InsertJob(SimpleLog::class.java, entry))
    }

    override fun update(entry: SimpleLog) {
        dbExecutor.execute(UpdateJob(SimpleLog::class.java, entry));
    }

    override fun localEntityFromRemote(remoteEntry: WorkLog): SimpleLog? {
        val remoteId = SimpleLogBuilder.parseUri(remoteEntry.self.toString())
        if (remoteId <= 0) return null
        val queryJob = QueryJob<SimpleLog>(SimpleLog::class.java, DBIndexable { "id = " + remoteId })
        dbExecutor.execute(queryJob)
        return queryJob.result()
    }

    override fun recreate(oldLocalEntry: SimpleLog, remoteEntry: WorkLog) {
        dbExecutor.execute(DeleteJob(SimpleLog::class.java, oldLocalEntry))
        dbExecutor.execute(
                InsertJob(
                        SimpleLog::class.java,
                        SimpleLogBuilder(oldLocalEntry.task, remoteEntry).build()
                )
        )
    }

    override fun markAsError(oldLocalEntry: SimpleLog, error: Throwable) {
        dbExecutor.execute(
                UpdateJob(
                        SimpleLog::class.java,
                        SimpleLogBuilder(oldLocalEntry).buildWithError(error.message)
                )
        )
    }

}