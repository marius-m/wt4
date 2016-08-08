package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.JiraException
import org.slf4j.LoggerFactory

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
        try {
            uploadValidator.valid(localLog)
            val outWorklog = remoteMergeClient.uploadLog(localLog)
            remoteMergeExecutor.recreateLog(localLog, outWorklog)
            logger.info("Success uploading $localLog!")
        } catch (e: JiraFilter.FilterErrorException) {
            logger.info("Skipping upload $localLog due to: ${e.message}")
            remoteMergeExecutor.markAsError(localLog, e)
        } catch (e: JiraException) {
            logger.info("Error uploading $localLog due to: ${e.message}")
            remoteMergeExecutor.markAsError(localLog, e)
        }
        return localLog
    }

    companion object {
        val logger = LoggerFactory.getLogger(RemoteLogPushImpl::class.java)!!
    }

}