package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
class RemoteLogPushImpl(
        private val remoteMergeClient: RemoteMergeClient,
        private val remoteMergeExecutor: RemoteMergeExecutor<SimpleLog, WorkLog>,
        private val uploadValidator: JiraFilter<SimpleLog>,
        private val localLog: SimpleLog
) : RemoteLogPush {

    override fun call(): SimpleLog {
        try {
            if (uploadValidator.valid(localLog)) {
                val outWorklog = remoteMergeClient.uploadLog(localLog)
                remoteMergeExecutor.recreate(localLog, outWorklog)
                logger.info("Success uploading: $localLog!")
            } else {
                logger.info("Not eligable for upload: $localLog")
            }
        } catch (e: JiraFilter.FilterErrorException) {
            logger.info("Error uploading: $localLog / (${e.message})")
            remoteMergeExecutor.markAsError(localLog, e)
        } catch (e: JiraException) {
            logger.info("Error uploading: $localLog / (${e.message})")
            remoteMergeExecutor.markAsError(localLog, e)
        }
        return localLog
    }

    companion object {
        val logger = LoggerFactory.getLogger(RemoteLogPushImpl::class.java)!!
    }

}