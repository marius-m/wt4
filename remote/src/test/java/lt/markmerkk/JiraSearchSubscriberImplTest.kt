package lt.markmerkk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.tickets.JiraSearchSubscriberImpl
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Single
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-09
 */
class JiraSearchSubscriberImplTest {
    val jiraClient: JiraClient = mock()
    val testSubscriber = TestSubscriber<Issue.SearchResult>()
    val jiraClientProvider: JiraClientProvider = mock()
    val userSettings: UserSettings = mock()
    val issueSearcher = JiraSearchSubscriberImpl(jiraClientProvider, userSettings)

    @Before
    fun setUp() {
        doReturn(Single.just(jiraClient)).whenever(jiraClientProvider).clientStream()
    }

    @Test
    fun noSearchResult_noValues() {
        // Arrange
        // Act
        issueSearcher.workedIssuesObservable(1000L, 2000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noJQL_throwError() {
        // Arrange
        // Act
        Observable.create(JiraSearchSubscriberImpl(jiraClientProvider, userSettings))
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertError(IllegalArgumentException::class.java)
        testSubscriber.assertNoValues()
    }

    @Test
    fun jiraError_noValues() {
        // Arrange
        whenever(jiraClient.searchIssues(any(), any(), any(), any())).thenThrow(JiraException("valid_exception"))

        // Act
        issueSearcher.workedIssuesObservable(1000L, 2000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertError(JiraException::class.java)
        testSubscriber.assertNoValues()
    }

    @Test
    fun nullIssues_noValues() {
        // Arrange
        val searchResult: Issue.SearchResult = mock()
        whenever(jiraClient.searchIssues(any(), any(), any(), any())).thenReturn(searchResult)

        // Act
        issueSearcher.workedIssuesObservable(1000L, 2000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noIssues_noValues() {
        // Arrange
        val issues = listOf<Issue>()
        val searchResult: Issue.SearchResult = mock()
        whenever(jiraClient.searchIssues(any(), any(), any(), any())).thenReturn(searchResult)
        searchResult.issues = issues

        // Act
        issueSearcher.workedIssuesObservable(1000L, 2000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
    }

    @Test
    fun valid_validValues() {
        // Arrange
        val issue: Issue = mock()
        val issues = listOf<Issue>(issue, issue, issue)
        val searchResult: Issue.SearchResult = mock()
        whenever(jiraClient.searchIssues(any(), any(), any(), any())).thenReturn(searchResult)
        searchResult.issues = issues

        // Act
        issueSearcher.workedIssuesObservable(1000L, 2000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        val results = testSubscriber.onNextEvents
        assertEquals(1, results.size)
        assertEquals(3, results[0].issues.size)
    }

    @Test
    fun validMorePages_validValues() {
        // Arrange
        val issue: Issue = mock()
        val issues = listOf<Issue>(issue, issue, issue)
        val searchResult1: Issue.SearchResult = mock()
        searchResult1.issues = issues
        searchResult1.total = 120
        searchResult1.max = 50
        val searchResult2: Issue.SearchResult = mock()
        searchResult2.issues = issues
        searchResult2.total = 120
        searchResult2.max = 50
        val searchResult3: Issue.SearchResult = mock()
        searchResult3.issues = issues
        searchResult3.total = 120
        searchResult3.max = 50
        whenever(jiraClient.searchIssues(any(), any(), any(), any())).thenReturn(searchResult1, searchResult2, searchResult3)

        // Act
        issueSearcher.workedIssuesObservable(1000L, 2000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        val results = testSubscriber.onNextEvents
        assertEquals(3, results.size)
    }
}