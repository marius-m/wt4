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
        val start: Long,
        val end: Long
) : JiraFilter<WorkLog> {

    override fun valid(input: WorkLog?): Boolean {
        try {
            val startDateTime = DateTime(start)
            val endDateTime = DateTime(end)
            if (input == null) throw FilterErrorException("Worklog is invalid")
            if (input.started == null) throw FilterErrorException("Worklog is invalid")
            if (input.author == null) throw FilterErrorException("Worklog is invalid")
            if (input.author.name != user) throw FilterErrorException("Worklog does not belong to the user")
            if (startDateTime.isAfter(input.started.time)) throw FilterErrorException("Start time is after worklog date")
            if (endDateTime.isBefore(input.started.time)) throw FilterErrorException("End time is before worklog date")
            return true
        } catch (e: FilterErrorException) {
            logger.debug("Ignoring " + input + " for because " + e.message)
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

