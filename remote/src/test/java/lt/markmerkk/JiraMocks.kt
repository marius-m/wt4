package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import net.rcarz.jiraclient.Issue

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

}