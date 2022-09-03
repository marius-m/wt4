package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.JiraException
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import java.lang.RuntimeException
import java.net.UnknownHostException

class WorklogApiFetchLogsTest {

    @Mock lateinit var jiraClientProvider: JiraClientProvider
    @Mock lateinit var jiraWorklogInteractor: JiraWorklogInteractor
    @Mock lateinit var ticketStorage: TicketStorage
    @Mock lateinit var worklogStorage: WorklogStorage
    @Mock lateinit var userSettings: UserSettings
    lateinit var worklogApi: WorklogApi

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        worklogApi = WorklogApi(
                timeProvider,
                jiraClientProvider,
                jiraWorklogInteractor,
                ticketStorage,
                worklogStorage
        )
    }

    @Test
    fun validFetch() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        val searchResultPair = ticket1 to listOf(worklog1)
        doReturn(Observable.just(searchResultPair))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(ticketStorage).insertOrUpdateSync(ticket1)
        verify(worklogStorage).insertOrUpdateSync(worklog1)
    }

    @Test
    fun valid_worklogStartSameNow() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(
                timeProvider,
                start = now
        )
        val searchResultPair = ticket1 to listOf(worklog1)
        doReturn(Observable.just(searchResultPair))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage).insertOrUpdateSync(worklog1)
    }

    @Test
    fun valid_worklogStartFromBeforeTime() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(
                timeProvider,
                start = now.minusDays(1)
        )
        val searchResultPair = ticket1 to listOf(worklog1)
        doReturn(Observable.just(searchResultPair))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage, never()).insertOrUpdateSync(worklog1)
    }

    @Test
    fun valid_worklogStartAfterEnd() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(
                timeProvider,
                start = now.plusDays(1)
        )
        val searchResultPair = ticket1 to listOf(worklog1)
        doReturn(Observable.just(searchResultPair))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(worklogStorage, never()).insertOrUpdateSync(worklog1)
    }

    @Test
    fun error() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        doReturn(Observable.error<Any>(RuntimeException()))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertNoErrors()
        fetchResult.assertCompleted()
        verify(ticketStorage, never()).insertOrUpdateSync(ticket1)
        verify(worklogStorage, never()).insertOrUpdateSync(worklog1)
    }

    @Test
    fun noNetwork() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        val noNetworkException = JiraException("no-network", UnknownHostException())
        doReturn(Observable.error<Any>(noNetworkException))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertError(UnknownHostException::class.java)
        verify(ticketStorage, never()).insertOrUpdateSync(ticket1)
        verify(worklogStorage, never()).insertOrUpdateSync(worklog1)
        verify(jiraClientProvider, never()).markAsError()
    }

    @Test
    fun jiraException() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        doReturn(Observable.error<Any>(JiraMocks.createJiraException()))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertError(JiraException::class.java)
        verify(ticketStorage, never()).insertOrUpdateSync(ticket1)
        verify(worklogStorage, never()).insertOrUpdateSync(worklog1)
        verify(jiraClientProvider, never()).markAsError()
    }

    @Test
    fun authError() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        val authException = JiraMocks.createAuthException()
        doReturn(Observable.error<Any>(authException))
                .whenever(jiraWorklogInteractor).searchWorklogs(any(), any(), any(), any())

        // Act
        val fetchResult = worklogApi.fetchLogs(
                fetchTime = now,
                start = now.toLocalDate(),
                end = now.plusDays(1).toLocalDate()
        ).test()

        // Assert
        fetchResult.assertError(AuthException::class.java)
        verify(ticketStorage, never()).insertOrUpdateSync(ticket1)
        verify(worklogStorage, never()).insertOrUpdateSync(worklog1)
        verify(jiraClientProvider).markAsError()
    }
}