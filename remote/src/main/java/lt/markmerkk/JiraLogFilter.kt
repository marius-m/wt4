package lt.markmerkk

import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 1/30/16.
 */
class JiraLogFilter(
        val user: String,
        val start: DateTime,
        val end: DateTime
) {


    fun valid(workLog: WorkLog?): Boolean {
        try {
            if (workLog == null) throw FilterErrorException("Worklog is invalid")
            if (workLog.started == null) throw FilterErrorException("Worklog is invalid")
            if (workLog.author == null) throw FilterErrorException("Worklog is invalid")
            if (workLog.author.name != user) throw FilterErrorException("Worklog does not belong to the user")
            if (start.isAfter(workLog.started.time)) throw FilterErrorException("Start time is after worklog date")
            if (end.isBefore(workLog.started.time)) throw FilterErrorException("End time is before worklog date")
            return true
        } catch (e: FilterErrorException) {
            logger.debug("Ignoring " + workLog + " for because " + e.message)
            return false
        }
    }

    //region Classes

    companion object {
        var logger: Logger = LoggerFactory.getLogger(JiraLogFilter::class.java)
    }

    /**
     * Thrown whenever there is a problem filtering some [WorkLog]
     */
    inner class FilterErrorException(message: String) : Exception(message)

    //endregion

}
