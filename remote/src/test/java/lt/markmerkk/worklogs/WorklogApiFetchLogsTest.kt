package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.RestException
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import java.lang.RuntimeException

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
                jiraClientProvider,
                jiraWorklogInteractor,
                ticketStorage,
                worklogStorage,
                userSettings
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
                .whenever(jiraWorklogInteractor).searchWorlogs(any(), any(), any(), any())

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
    fun error() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        doReturn(Observable.error<Any>(RuntimeException()))
                .whenever(jiraWorklogInteractor).searchWorlogs(any(), any(), any(), any())

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
    fun authError() {
        // Assemble
        val now = timeProvider.now()
        val ticket1 = Mocks.createTicket(id = 1)
        val worklog1 = Mocks.createBasicLogRemote(timeProvider)
        val authException = JiraMocks.createAuthException()
        doReturn(Observable.error<Any>(authException))
                .whenever(jiraWorklogInteractor).searchWorlogs(any(), any(), any(), any())

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
    }
}