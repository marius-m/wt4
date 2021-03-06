package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.exceptions.AuthException
import lt.markmerkk.schema1.tables.Ticket
import net.rcarz.jiraclient.JiraClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single
import java.lang.RuntimeException

class TicketsNetworkRepoSearchRemoteTicketsTest {

    @Mock lateinit var jiraClientProvider: JiraClientProvider
    @Mock lateinit var jiraTicketSearch: JiraTicketSearch
    @Mock lateinit var ticketsDatabaseRepo: TicketStorage
    @Mock lateinit var jiraClient: JiraClient
    @Mock lateinit var userSettings: UserSettings
    lateinit var ticketApi: TicketApi

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketApi = TicketApi(
                jiraClientProvider,
                jiraTicketSearch,
                ticketsDatabaseRepo,
                userSettings
        )
        doReturn("valid_jql").whenever(userSettings).issueJql
    }

    @Test
    fun validSearch() {
        // Assemble
        val apiTickets = listOf(
                Mocks.createTicket(id = 1, code = "DEV-111"),
                Mocks.createTicket(id = 2, code = "DEV-222"),
                Mocks.createTicket(id = 3, code = "DEV-333")
        )
        val dbTickets = listOf(
                Mocks.createTicket(code = "DEV-111"),
                Mocks.createTicket(code = "DEV-222"),
                Mocks.createTicket(code = "DEV-333")
        )
        doReturn(jiraClient).whenever(jiraClientProvider).client()
        doReturn(Observable.from(apiTickets)).whenever(jiraTicketSearch).searchIssues(any(), any(), any())
        doReturn(Single.just(dbTickets)).whenever(ticketsDatabaseRepo).loadTickets()
        doReturn(Single.just(1)).whenever(ticketsDatabaseRepo).insertOrUpdate(any())

        // Act
        val result = ticketApi.searchRemoteTicketsAndCache(now = TimeMachine.now())
                .test()

        // Assert
        result.assertNoErrors()
        result.assertValueCount(1)
        val resultItems = result.onNextEvents.first()
        assertThat(resultItems.size).isEqualTo(3)

        verify(ticketsDatabaseRepo, times(3)).insertOrUpdateSync(any())
        verify(ticketsDatabaseRepo, atLeastOnce()).loadTickets()
    }

    @Test
    fun clientError() {
        // Assemble
        doThrow(AuthException(RuntimeException())).whenever(jiraClientProvider).client()

        // Act
        val result = ticketApi.searchRemoteTicketsAndCache(now = TimeMachine.now())
                .test()

        // Assert
        result.assertError(AuthException::class.java)
        verifyZeroInteractions(ticketsDatabaseRepo)
    }

    @Test
    fun noTickets() {
        // Assemble
        val remoteIssues = emptyList<Ticket>()
        val dbTickets = listOf(
                Mocks.createTicket(),
                Mocks.createTicket(),
                Mocks.createTicket()
        )
        doReturn(jiraClient).whenever(jiraClientProvider).client()
        doReturn(Observable.from(remoteIssues)).whenever(jiraTicketSearch).searchIssues(any(), any(), any())
        doReturn(Single.just(dbTickets)).whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        val result = ticketApi.searchRemoteTicketsAndCache(now = TimeMachine.now())
                .test()

        // Assert
        result.assertNoErrors()
        result.assertValueCount(1)
        val resultItems = result.onNextEvents.first()
        assertThat(resultItems.size).isEqualTo(3)

        verify(ticketsDatabaseRepo, times(0)).insertOrUpdate(any())
        verify(ticketsDatabaseRepo, atLeastOnce()).loadTickets()
    }

}