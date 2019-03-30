package lt.markmerkk

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.tickets.JiraSearchSubscriber
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.WorkLog
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single
import rx.schedulers.Schedulers

class JiraInteractorImplRemoteWorksTest {

    @Mock lateinit var clientProvider: JiraClientProvider
    @Mock lateinit var searchSubscriber: JiraSearchSubscriber
    @Mock lateinit var worklogSubscriber: JiraWorklogSubscriber
    @Mock lateinit var dataStorage: IDataStorage<SimpleLog>
    @Mock lateinit var jiraClient: JiraClient

    lateinit var interactor: JiraInteractorImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = JiraInteractorImpl(
                clientProvider,
                dataStorage,
                searchSubscriber,
                worklogSubscriber
        )
        doReturn(
                listOf(
                        SimpleLog(),
                        SimpleLog(),
                        SimpleLog()
                )
        ).whenever(dataStorage).data
        doReturn(Single.just(jiraClient)).whenever(clientProvider).clientStream()
    }

    @Test
    fun valid_emitListWithItems() {
        // Assemble
        val fakeJiraWork = fakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.workedIssuesObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(fakeJiraWork, fakeJiraWork, fakeJiraWork)))

        // Act
        val result = interactor.jiraRemoteWorks(1000, 2000)
                .test()


        // Assert
        result.assertNoErrors()
        result.assertValueCount(1) // list with items
    }

    @Test
    fun valid_emitItems() {
        // Assemble
        val fakeJiraWork = fakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.workedIssuesObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(fakeJiraWork, fakeJiraWork, fakeJiraWork)))


        // Act
        val result = interactor.jiraRemoteWorks(1000, 2000)
                .test()


        // Assert
        assertEquals(3, result.onNextEvents[0].size)
    }

    @Test
    fun invalidItems_emitEmptyList() {
        // Assemble
        val invalidJiraWork = invalidFakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.workedIssuesObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(invalidJiraWork, invalidJiraWork)))


        // Act
        val result = interactor.jiraRemoteWorks(1000, 2000)
                .test()


        // Assert
        assertEquals(0, result.onNextEvents[0].size)
    }

    @Test
    fun errorClient_throwError() {
        // Assemble
        val invalidJiraWork = invalidFakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.workedIssuesObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(invalidJiraWork, invalidJiraWork)))
        doReturn(Single.error<Any>(IllegalStateException())).whenever(clientProvider).clientStream()

        // Act
        val result = interactor.jiraRemoteWorks(1000, 2000)
                .test()

        // Assert
        result.assertError(IllegalStateException::class.java)
    }

    // Convenience

    fun fakeJiraWork(): JiraWork {
        val fakeIssue: Issue = mock()
        whenever(fakeIssue.key).thenReturn("valid_key")
        val fakeWorklog: WorkLog = mock()
        return JiraWork(fakeIssue, listOf(fakeWorklog))
    }

    fun invalidFakeJiraWork(): JiraWork {
        val fakeIssue: Issue = mock() // Issue key null
        val fakeWorklog: WorkLog = mock()
        return JiraWork(fakeIssue, listOf(fakeWorklog))
    }

}