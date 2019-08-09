package lt.markmerkk

import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 1/30/16.
 * Assures that [WorkLog] is valid for download and local storage
 */
class JiraFilterWorklog(
        val user: String,
        val start: Long,
        val end: Long
) : JiraFilter<WorkLog> {

    @Throws(JiraFilter.FilterErrorException::class)
    override fun valid(input: WorkLog?): Boolean {
        val startDateTime = DateTime(start)
        val endDateTime = DateTime(end)
        if (input == null) throw JiraFilter.FilterErrorException("Worklog is invalid")
        if (input.started == null) throw JiraFilter.FilterErrorException("Worklog is invalid")
        if (input.author == null) throw JiraFilter.FilterErrorException("Worklog is invalid")
        if (input.author.email != user) throw JiraFilter.FilterErrorException("Worklog does not belong to the user")
        if (startDateTime.isAfter(input.started.time)) throw JiraFilter.FilterErrorException("Start time is after worklog date")
        if (endDateTime.isBefore(input.started.time)) throw JiraFilter.FilterErrorException("End time is before worklog date")
        return true
    }

    //region Classes

    companion object {
        var logger: Logger = LoggerFactory.getLogger(Tags.JIRA)
    }

    //endregion

}

