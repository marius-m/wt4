package lt.markmerkk.merger

import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.utils.UriUtils
import net.rcarz.jiraclient.WorkLog

class RemoteLogMergeExecutorImpl(
        private val worklogRepository: WorklogRepository,
        private val timeProvider: TimeProvider
) : RemoteMergeExecutor<SimpleLog, WorkLog> {

    override fun create(entry: SimpleLog) {
//        dbExecutor.execute(InsertJob(SimpleLog::class.java, entry))
        val log = entry.toLog(timeProvider)
        worklogRepository.insertOrUpdateSync(log)
   }

    override fun update(entry: SimpleLog) {
//        dbExecutor.execute(UpdateJob(SimpleLog::class.java, entry));
        val log = entry.toLog(timeProvider)
        worklogRepository.updateSync(log)
    }

    override fun localEntityFromRemote(remoteEntry: WorkLog): SimpleLog? {
//        val remoteId = SimpleLogBuilder.parseUri(remoteEntry.self.toString())
//        if (remoteId <= 0) return null
//        val queryJob = QueryJob<SimpleLog>(SimpleLog::class.java, DBIndexable { "id = " + remoteId })
//        dbExecutor.execute(queryJob)
//        return queryJob.result()
        val remoteId = UriUtils.parseUri(remoteEntry.url)
        return worklogRepository.findByRemoteId(remoteId)
                .toLegacyLogOrNull(timeProvider)
    }

    override fun recreate(oldLocalEntry: SimpleLog, remoteEntry: WorkLog) {
//        dbExecutor.execute(DeleteJob(SimpleLog::class.java, oldLocalEntry))
//        dbExecutor.execute(
//                InsertJob(
//                        SimpleLog::class.java,
//                        SimpleLogBuilder(oldLocalEntry.task, remoteEntry).build()
//                )
//        )
        val newEntry = SimpleLogBuilder(oldLocalEntry.task, remoteEntry).build()
                .toLog(timeProvider)
        worklogRepository.reinsert(oldLocalEntry._id, newEntry)
    }

    override fun markAsError(oldLocalEntry: SimpleLog, error: Throwable) {
//        dbExecutor.execute(
//                UpdateJob(
//                        SimpleLog::class.java,
//                        SimpleLogBuilder(oldLocalEntry).buildWithError(error.message)
//                )
//        )
        val oldEntryAsError = SimpleLogBuilder(oldLocalEntry).buildWithError(error.message)
                .toLog(timeProvider)
        worklogRepository.update(oldEntryAsError)
    }

}