package lt.markmerkk

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-09
 */
class JiraWorklogSubscriberImplTest {
    val testSubscriber = TestSubscriber<JiraWork>()
    val jiraClient: JiraClient = mock()
    val searchResult: Issue.SearchResult = mock()
    val jiraClientProvider: JiraClientProvider = mock()

    @Before
    fun setUp() {
        doReturn(jiraClient).whenever(jiraClientProvider).client()
    }

    @Test
    fun valid_returnValue() {
        // Arrange
        val worklog: WorkLog = mock()
        val issue: Issue = mock()
        doReturn("valid_key").whenever(issue).key
        val worklogs: List<WorkLog> = listOf(worklog, worklog, worklog)
        doReturn(worklogs).whenever(issue).allWorkLogs
        searchResult.issues = listOf(issue)
        doReturn(issue).whenever(jiraClient).getIssue(any())
        val subscriber = JiraWorklogSubscriberImpl(jiraClientProvider)

        // Act
        subscriber.worklogResultObservable(searchResult)
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        val result = testSubscriber.onNextEvents
        assertEquals(1, result.size)
        assertTrue(result[0].valid())
        assertEquals(3, result[0].worklogs!!.size)
    }

    @Test
    fun invalidIssue_returnInvalidValue() {
        // Arrange
        val issue: Issue = mock()
        searchResult.issues = listOf(issue)
        val subscriber = JiraWorklogSubscriberImpl(jiraClientProvider)

        // Act
        subscriber.worklogResultObservable(searchResult)
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        val result = testSubscriber.onNextEvents
        assertEquals(1, result.size)
        assertFalse(result[0].valid())
    }

    @Test
    fun jiraErrorGetIssues_noValues() {
        // Arrange
        val issue: Issue = mock()
        doThrow(JiraException("valid_exception")).whenever(jiraClient).getIssue(any())
        searchResult.issues = listOf(issue)
        val subscriber = JiraWorklogSubscriberImpl(jiraClientProvider)

        // Act
        subscriber.worklogResultObservable(searchResult)
            .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
    }

    @Test
    fun nullList_returnEmpty() {
        // Arrange
        val subscriber = JiraWorklogSubscriberImpl(jiraClientProvider)

        // Act
        subscriber.worklogResultObservable(searchResult)
            .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
    }

    @Test
    fun emptyList_returnEmpty() {
        // Arrange
        searchResult.issues = emptyList()
        val subscriber = JiraWorklogSubscriberImpl(jiraClientProvider)

        // Act
        subscriber.worklogResultObservable(searchResult)
            .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
    }
}