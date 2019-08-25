package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.RestException

object JiraMocks {

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

}