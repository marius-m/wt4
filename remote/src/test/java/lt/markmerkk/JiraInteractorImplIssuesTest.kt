package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.tickets.JiraSearchSubscriber
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class JiraInteractorImplIssuesTest {

    @Mock lateinit var clientProvider: JiraClientProvider
    @Mock lateinit var localStorage: IDataStorage<SimpleLog>
    @Mock lateinit var issueStorage: IDataStorage<LocalIssue>
    @Mock lateinit var searchSubscriber: JiraSearchSubscriber
    @Mock lateinit var worklogSubscriber: JiraWorklogSubscriber
    @Mock lateinit var validSearchResult: Issue.SearchResult
    @Mock lateinit var jiraClient: JiraClient

    lateinit var interactor: JiraInteractorImpl

    val testSubscriber = TestSubscriber<List<Issue>>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = JiraInteractorImpl(
                clientProvider,
                localStorage,
                issueStorage,
                searchSubscriber,
                worklogSubscriber,
                Schedulers.immediate()
        )
        doReturn(Single.just(jiraClient)).whenever(clientProvider).clientStream()
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
        doReturn(Single.error<Any>(IllegalStateException())).whenever(clientProvider).clientStream()

        // Act
        interactor.jiraIssues()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertError(IllegalStateException::class.java)
    }
}