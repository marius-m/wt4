package lt.markmerkk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.IDataStorage
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class JiraInteractorImplRemoteWorksTest {
    val testSubscriber = TestSubscriber<List<JiraWork>>()
    val clientProvider: JiraClientProvider = mock()
    val searchSubscriber: JiraSearchSubscriber = mock()
    val worklogSubscriber: JiraWorklogSubscriber = mock()
    val dataStorage: IDataStorage<SimpleLog> = mock()
    val interactor = JiraInteractorImpl(
            clientProvider,
            dataStorage,
            searchSubscriber,
            worklogSubscriber,
            Schedulers.immediate()
    )

    @Before
    fun setUp() {
        doReturn(
                listOf(
                        SimpleLog(),
                        SimpleLog(),
                        SimpleLog()
                )
        ).whenever(dataStorage).dataAsList
    }

    @Test
    fun valid_emitListWithItems() {
        // Assemble
        val fakeJiraWork = fakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.searchResultObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(fakeJiraWork, fakeJiraWork, fakeJiraWork)))

        // Act
        interactor.jiraRemoteWorks(1000, 2000)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)


        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1) // list with items
    }

    @Test
    fun valid_emitItems() {
        // Assemble
        val fakeJiraWork = fakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.searchResultObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(fakeJiraWork, fakeJiraWork, fakeJiraWork)))


        // Act
        interactor.jiraRemoteWorks(1000, 2000)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)


        // Assert
        assertEquals(3, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun invalidItems_emitEmptyList() {
        // Assemble
        val invalidJiraWork = invalidFakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.searchResultObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(invalidJiraWork, invalidJiraWork)))


        // Act
        interactor.jiraRemoteWorks(1000, 2000)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)


        // Assert
        assertEquals(0, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun errorClient_throwError() {
        // Assemble
        val invalidJiraWork = invalidFakeJiraWork()
        val fakeIssueResult: Issue.SearchResult = mock()
        whenever(searchSubscriber.searchResultObservable(any(), any()))
                .thenReturn(Observable.just(fakeIssueResult))
        whenever(worklogSubscriber.worklogResultObservable(any()))
                .thenReturn(Observable.from(listOf(invalidJiraWork, invalidJiraWork)))
        whenever(clientProvider.client()).thenThrow(IllegalStateException("error_client"))

        // Act
        interactor.jiraRemoteWorks(1000, 2000)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertError(IllegalStateException::class.java)
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