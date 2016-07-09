package lt.markmerkk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-09
 */
class JiraInteractorImplJiraWorksTest {
    val testSubscriber = TestSubscriber<List<JiraWork>>()
    val jiraClient: JiraClient = mock()
    val jiraSearchSubscriber: JiraSearchSubsciber = mock()
    val jiraWorklogSubscriber: JiraWorklogSubscriber = mock()
    val jiraClientProvider: JiraClientProvider = mock()
    val interactor = JiraInteractorImpl(
            jiraClientProvider,
            jiraSearchSubscriber,
            jiraWorklogSubscriber
    )

    @Before
    fun setUp() {
        doReturn(Observable.just(jiraClient)).whenever(jiraClientProvider).clientObservable()
    }

    @Test
    fun valid_returnValid() {
        // Arrange
        val issue: Issue = mock()
        val work = JiraWork(issue, emptyList())
        val searchResult: Issue.SearchResult = mock()
        doReturn(Observable.just(searchResult)).whenever(jiraSearchSubscriber).searchResultObservable(any(), any())
        doReturn(Observable.just(work)).whenever(jiraWorklogSubscriber).worklogResultObservable(any())

        // Act
        interactor.jiraWorks(DateTime(1000), DateTime(2000))
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        val result = testSubscriber.onNextEvents
        assertEquals(1, result[0].size)
    }

}