package lt.markmerkk.merger

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.TimeSplit
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
class RemoteMergeClientImpl(
        private val jiraClientProvider: JiraClientProvider
) : RemoteMergeClient {

    @Throws(JiraException::class)
    override fun uploadLog(simpleLog: SimpleLog): WorkLog {
        val issue = jiraClientProvider
                .client()
                .getIssue(simpleLog.task)
        val comment = TimeSplit.addStamp(
                simpleLog.start,
                simpleLog.end,
                simpleLog.comment
        )
        val remoteLog = issue.addWorkLog(
                comment,
                DateTime(simpleLog.start),
                simpleLog.duration / 1000
        )
        logger.info("Success adding $issue worklog!")
        return remoteLog
    }

    companion object {
        val logger = LoggerFactory.getLogger(RemoteMergeClientImpl::class.java)!!
    }

}