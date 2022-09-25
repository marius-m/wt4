package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.RestException
import net.rcarz.jiraclient.User
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.joda.time.Duration
import java.lang.RuntimeException

object JiraMocks {

    fun createJiraUserEmpty(): JiraUser {
        return createJiraUser(name = "", displayName = "", email = "", accountId = "")
    }

    fun createJiraUser(
            name: String = "name",
            displayName: String = "display_name",
            email: String = "email",
            accountId: String = "account_id"
    ): JiraUser {
        return JiraUser(
                name = name,
                displayName = displayName,
                email = email,
                accountId = accountId
        )
    }

    fun mockJiraClient(): JiraClient {
        val jiraClient: JiraClient = mock()
        return jiraClient
    }

    fun mockJiraIssue(
            key: String = "TT2-123",
            summary: String = "valid_summary",
            idUrl: String = "https://jira.com/12345",
            uri: String = "https://jira.com"
    ): Issue {
        val issue: Issue = mock()
        doReturn(key).whenever(issue).key
        doReturn(summary).whenever(issue).summary
        doReturn(idUrl).whenever(issue).id
        doReturn(uri).whenever(issue).url
        return issue
    }

    fun createRestException(
            status: Int,
            message: String
    ): JiraException {
        val restException = RestException(
                message,
                status,
                "detailed_$message",
                emptyArray()
        )
        return JiraException(restException.message, restException)
    }

    fun createAuthException(): JiraException {
        val restException = RestException(
                "Authorization error",
                401,
                "auth_error_details",
                emptyArray()
        )
        return JiraException(restException.message, restException)
    }

    fun createJiraException(): JiraException {
        return JiraException("jira_exception", RuntimeException())
    }

    fun mockWorklog(
        timeProvider: TimeProvider,
        author: User? = mockAuthor(),
        created: DateTime? = timeProvider.now(),
        updated: DateTime? = timeProvider.now(),
        started: DateTime? = timeProvider.now(),
        url: String = "https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051",
        comment: String? = "",
        durationTimeSpent: Duration = Duration.standardMinutes(10),
    ): WorkLog {
        val worklog: WorkLog = mock()
        doReturn(author).whenever(worklog).author
        doReturn(comment).whenever(worklog).comment
        doReturn(created?.toDate()).whenever(worklog).createdDate
        doReturn(updated?.toDate()).whenever(worklog).updatedDate
        doReturn(started?.toDate()).whenever(worklog).started
        doReturn(url).whenever(worklog).url
        doReturn(durationTimeSpent.toStandardSeconds().seconds).whenever(worklog).timeSpentSeconds
        return worklog
    }

    fun mockAuthor(
        name: String = "name",
        email: String = "email@mail.com",
        displayName: String = "Display name",
        accountId: String = "account_id",
    ): User {
        val author: User = mock()
        doReturn(email).whenever(author).email
        doReturn(displayName).whenever(author).displayName
        doReturn(name).whenever(author).name
        doReturn(accountId).whenever(author).accountId
        return author
    }
}