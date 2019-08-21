package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.schema1.tables.Ticket
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single

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
                Mocks.createTicket(id = 1),
                Mocks.createTicket(id = 2),
                Mocks.createTicket(id = 3)
        )
        val dbTickets = listOf(
                Mocks.createTicket(),
                Mocks.createTicket(),
                Mocks.createTicket()
        )
        doReturn(Single.just(jiraClient)).whenever(jiraClientProvider).clientStream()
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

        verify(ticketsDatabaseRepo, times(3)).insertOrUpdate(any())
        verify(ticketsDatabaseRepo).loadTickets()
    }

    @Test
    fun clientError() {
        // Assemble
        doReturn(Single.error<Any>(IllegalArgumentException())).whenever(jiraClientProvider).clientStream()

        // Act
        val result = ticketApi.searchRemoteTicketsAndCache(now = TimeMachine.now())
                .test()

        // Assert
        result.assertError(java.lang.IllegalArgumentException::class.java)
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
        doReturn(Single.just(jiraClient)).whenever(jiraClientProvider).clientStream()
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
        verify(ticketsDatabaseRepo).loadTickets()
    }

}