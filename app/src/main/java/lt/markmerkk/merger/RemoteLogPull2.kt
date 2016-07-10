package lt.markmerkk.merger

import lt.markmerkk.entities.JiraWork
import lt.markmerkk.storage2.SimpleLogBuilder

/**
 * @author mariusmerkevicius
 * @since 2016-07-10
 */
class RemoteLogPull2 (
        val mergeExecutor: RemoteMergeExecutor,
        val remoteIssue: JiraWork
) : Runnable {

    override fun run() {
        if (!remoteIssue.valid()) return
        val worklogs = remoteIssue.worklogs ?: return
        for (remoteWorklog in worklogs) {
            if (!remoteIssue.validWorklog(remoteWorklog)) continue
            val oldLog = mergeExecutor.localEntityFromRemote(remoteWorklog)
            if (oldLog == null) {
                mergeExecutor.createLog(SimpleLogBuilder(remoteIssue.issue?.key, remoteWorklog).build())
            } else {
                mergeExecutor.updateLog(oldLog)
            }
        }
    }

}