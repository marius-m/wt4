package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraLogFilter
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.storage2.SimpleLogBuilder
import net.rcarz.jiraclient.WorkLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-07-10
 * Responsible for merging / updating database [SimpleLog] entities with the remote ones.
 *
 * This class should follow these rules for merging
 * 1. Create new local log if there is not current one
 * 2. Update local log with the data from sever
 * 3. All pulled data should contain dirty = 0.
 */
class RemoteLogMergerImpl(
        val mergeExecutor: RemoteMergeExecutor,
        val remoteLogFilter: JiraFilter<WorkLog>,
        val remoteIssue: JiraWork
) : RemoteLogMerger {

    override fun call(): JiraWork {
        if (!remoteIssue.valid()) return remoteIssue
        val worklogs = remoteIssue.worklogs ?: return remoteIssue
        for (remoteWorklog in worklogs) {
            if (!remoteIssue.validWorklog(remoteWorklog)) {
                logger.info("Invalid worklog: $remoteWorklog")
                continue
            }
            if (!remoteLogFilter.valid(remoteWorklog)) {
                logger.info("Filtering out as invalid worklog: $remoteWorklog")
                continue
            }
            val oldLog = mergeExecutor.localEntityFromRemote(remoteWorklog)
            if (oldLog == null) {
                mergeExecutor.createLog(SimpleLogBuilder(remoteIssue.issue!!.key, remoteWorklog).build())
                logger.info("Creating new log: $remoteWorklog")
            } else {
                mergeExecutor.updateLog(SimpleLogBuilder(oldLog, remoteIssue.issue!!.key, remoteWorklog).build())
                logger.info("Updating old log with: $remoteWorklog")
            }
        }
        return remoteIssue
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger("RemoteLogPull2")
    }

}