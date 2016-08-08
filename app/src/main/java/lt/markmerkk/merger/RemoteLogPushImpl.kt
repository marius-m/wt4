package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
class RemoteLogPushImpl(
        private val remoteMergeClient: RemoteMergeClient,
        private val remoteMergeExecutor: RemoteMergeExecutor,
        private val uploadValidator: JiraFilter<SimpleLog>,
        private val localLog: SimpleLog
) : RemoteLogPush {

    override fun call(): SimpleLog {
        val outWorklog = remoteMergeClient.uploadLog(localLog)
        if (outWorklog != null) {
            remoteMergeExecutor.recreateLog(localLog, outWorklog)
        }
        return localLog
    }

}