package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.IDataStorage
import net.rcarz.jiraclient.Issue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class JiraInteractorImplIssuesTest {

    val clientProvider: JiraClientProvider = mock()
    val localStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()
    val searchSubscriber: JiraSearchSubscriber = mock()
    val worklogSubscriber: JiraWorklogSubscriber = mock()
    val interactor = JiraInteractorImpl(
            clientProvider,
            localStorage,
            issueStorage,
            searchSubscriber,
            worklogSubscriber,
            Schedulers.immediate()
    )

    val testSubscriber = TestSubscriber<List<Issue>>()
    val validSearchResult: Issue.SearchResult = mock()

    @Before
    fun setUp() {

    }

    @Test
    fun valid_emitItems() {
        // Arrange
        val fakeIssue: Issue = mock()
        validSearchResult.issues = listOf(
                fakeIssue, fakeIssue, fakeIssue
        )
        whenever(searchSubscriber.userIssuesObservable())
            .thenReturn(Observable.just(validSearchResult))

        // Act
        interactor.jiraIssues()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1)
        assertEquals(3, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun multipleEmissions_emitItems() {
        // Arrange
        val fakeIssue: Issue = mock()
        validSearchResult.issues = listOf(fakeIssue, fakeIssue, fakeIssue)
        whenever(searchSubscriber.userIssuesObservable())
            .thenReturn(
                    Observable.from(listOf(
                            validSearchResult,
                            validSearchResult,
                            validSearchResult
                    ))
            )

        // Act
        interactor.jiraIssues()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1)
        assertEquals(9, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun noItems_emitItems() {
        // Arrange
        val fakeIssue: Issue = mock()
        validSearchResult.issues = listOf(fakeIssue, fakeIssue, fakeIssue)
        whenever(searchSubscriber.userIssuesObservable())
            .thenReturn(
                    Observable.empty()
            )

        // Act
        interactor.jiraIssues()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1)
        assertEquals(0, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun clientError_throwError() {
        // Arrange
        val fakeIssue: Issue = mock()
        validSearchResult.issues = listOf(fakeIssue, fakeIssue, fakeIssue)
        whenever(searchSubscriber.userIssuesObservable())
            .thenReturn(Observable.empty())
        whenever(clientProvider.client()).thenThrow(IllegalStateException("client_error"))

        // Act
        interactor.jiraIssues()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertError(IllegalStateException::class.java)
    }
}