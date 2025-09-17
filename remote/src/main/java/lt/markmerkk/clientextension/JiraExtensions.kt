package lt.markmerkk.clientextension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import java.net.URI
import lt.markmerkk.JiraUser
import lt.markmerkk.WorklogUtils
import net.rcarz.jiraclient.Field
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.Resource
import net.rcarz.jiraclient.RestClient
import net.rcarz.jiraclient.User
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

fun User?.toJiraUser(): JiraUser {
    if (this == null) {
        return JiraUser.Companion.asEmpty()
    }
    return JiraUser(
        name = this.name ?: "",
        displayName = this.displayName ?: "",
        email = this.email ?: "",
    )
}

fun Issue.getRestUri(key: String?): String {
    return Resource.getBaseUri() + "issue/" + (key ?: "")
}

@Throws(JiraException::class)
fun Issue.addWorklog(
    restClientExternal: RestClient,
    comment: String,
    startDate: DateTime,
    timeSpentSeconds: Long,
): WorkLog {
    try {
        if (timeSpentSeconds < 60) // We do not add a worklog that duration is below a minute
            throw IllegalArgumentException("Time spent cannot be lower than 1 minute.");

        val worklogMap = mapOf<String, JsonNode>(
            Pair("comment", TextNode(comment)),
            Pair("started", TextNode(DateTimeFormat.forPattern(Field.DATETIME_FORMAT).print(startDate.getMillis()))),
            Pair("timeSpent", TextNode(WorklogUtils.formatDurationFromSeconds(timeSpentSeconds))),
        )
        val req = ObjectNode(JsonNodeFactory.instance, worklogMap)

        val result = restClientExternal.post("${getRestUri(key)}/worklog", req);
        return WorkLogExt(restClientExternal, result);
    } catch (ex: Exception) {
        throw JiraException("Failed add worklog to issue $key", ex);
    }
}

/**
 * Removes a worklog
 *
 * @param worklogId attachment id to remove
 *
 * @throws JiraException when the attachment removal fails
 */
@Throws(JiraException::class)
fun Issue.removeWorklog(
    restClientExternal: RestClient,
    worklogId: String,
) {
    try {
        val uri: URI? = restClientExternal.buildURI(getRestUri(key) + "/worklog/" + worklogId)
        restClientExternal.delete(uri)
    } catch (ex: java.lang.Exception) {
        throw JiraException("Failed remove worklog $worklogId", ex)
    }
}
