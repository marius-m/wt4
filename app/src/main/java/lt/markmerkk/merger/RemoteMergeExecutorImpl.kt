package lt.markmerkk.merger

import lt.markmerkk.storage2.SimpleLog
import lt.markmerkk.storage2.SimpleLogBuilder
import lt.markmerkk.storage2.database.interfaces.DBIndexable
import lt.markmerkk.storage2.database.interfaces.IExecutor
import lt.markmerkk.storage2.jobs.DeleteJob
import lt.markmerkk.storage2.jobs.InsertJob
import lt.markmerkk.storage2.jobs.QueryJob
import lt.markmerkk.storage2.jobs.UpdateJob
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-07-10
 */
class RemoteMergeExecutorImpl(
        val dbExecutor: IExecutor
) : RemoteMergeExecutor {

    override fun createLog(simpleLog: SimpleLog) {
        dbExecutor.execute(InsertJob(SimpleLog::class.java, simpleLog))
    }

    override fun updateLog(simpleLog: SimpleLog) {
        dbExecutor.execute(UpdateJob(SimpleLog::class.java, simpleLog));
    }

    override fun localEntityFromRemote(remoteWorklog: WorkLog): SimpleLog? {
        val remoteId = SimpleLogBuilder.parseUri(remoteWorklog.self.toString())
        if (remoteId <= 0) return null
        val queryJob = QueryJob<SimpleLog>(SimpleLog::class.java, DBIndexable { "id = " + remoteId })
        dbExecutor.execute(queryJob)
        return queryJob.result()
    }

    override fun recreateLog(oldLocalLog: SimpleLog, remoteWorklog: WorkLog) {
        dbExecutor.execute(DeleteJob(SimpleLog::class.java, oldLocalLog))
        dbExecutor.execute(
                InsertJob(
                        SimpleLog::class.java,
                        SimpleLogBuilder(oldLocalLog.task, remoteWorklog).build()
                )
        )
    }

    override fun markAsError(oldLocalLog: SimpleLog, error: Throwable) {
        dbExecutor.execute(
                UpdateJob(
                        SimpleLog::class.java,
                        SimpleLogBuilder(oldLocalLog).buildWithError(error.message)
                )
        )
    }

}