package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.*
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single

class TicketsInteractorSearchRemoteTicketsTest {

    @Mock lateinit var jiraClientProvider: JiraClientProvider
    @Mock lateinit var jiraTicketSearch: JiraTicketSearch
    @Mock lateinit var databaseRepository: DatabaseRepository
    @Mock lateinit var jiraClient: JiraClient
    @Mock lateinit var userSettings: UserSettings
    lateinit var ticketsInteractor: TicketsInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketsInteractor = TicketsInteractor(
                jiraClientProvider,
                jiraTicketSearch,
                databaseRepository,
                userSettings
        )
        doReturn("valid_jql").whenever(userSettings).issueJql
    }

    @Test
    fun validSearch() {
        // Assemble
        val remoteIssues = listOf(
                JiraMocks.mockJiraIssue(),
                JiraMocks.mockJiraIssue(),
                JiraMocks.mockJiraIssue()
        )
        val dbTickets = listOf(
                Mocks.createTicket(),
                Mocks.createTicket(),
                Mocks.createTicket()
        )
        doReturn(Single.just(jiraClient)).whenever(jiraClientProvider).clientStream()
        doReturn(Observable.just(remoteIssues)).whenever(jiraTicketSearch).searchIssues(any(), any())
        doReturn(dbTickets).whenever(databaseRepository).loadTickets()

        // Act
        val result = ticketsInteractor.searchRemoteTickets(now = TimeMachine.now())
                .test()

        // Assert
        result.assertNoErrors()
        result.assertValueCount(1)
        val resultItems = result.onNextEvents.first()
        assertThat(resultItems.size).isEqualTo(3)

        verify(databaseRepository, times(3)).insertOrUpdate(any())
        verify(databaseRepository).loadTickets()
    }

    @Test
    fun validSearch_multiplePages() {
        // Assemble
        val remoteIssuesPage1 = listOf(
                JiraMocks.mockJiraIssue(),
                JiraMocks.mockJiraIssue(),
                JiraMocks.mockJiraIssue()
        )
        val remoteIssuesPage2 = listOf(
                JiraMocks.mockJiraIssue(),
                JiraMocks.mockJiraIssue(),
                JiraMocks.mockJiraIssue()
        )
        val dbTickets = listOf(
                Mocks.createTicket(),
                Mocks.createTicket(),
                Mocks.createTicket()
        )
        doReturn(Single.just(jiraClient)).whenever(jiraClientProvider).clientStream()
        doReturn(Observable.from(listOf(remoteIssuesPage1, remoteIssuesPage2)))
                .whenever(jiraTicketSearch).searchIssues(any(), any())
        doReturn(dbTickets).whenever(databaseRepository).loadTickets()

        // Act
        val result = ticketsInteractor.searchRemoteTickets(now = TimeMachine.now())
                .test()

        // Assert
        result.assertNoErrors()
        result.assertValueCount(1)

        verify(databaseRepository, times(6)).insertOrUpdate(any())
        verify(databaseRepository).loadTickets()
    }

    @Test
    fun clientError() {
        // Assemble
        doReturn(Single.error<Any>(IllegalArgumentException())).whenever(jiraClientProvider).clientStream()

        // Act
        val result = ticketsInteractor.searchRemoteTickets(now = TimeMachine.now())
                .test()

        // Assert
        result.assertError(java.lang.IllegalArgumentException::class.java)
        verifyZeroInteractions(databaseRepository)
    }

    @Test
    fun noTickets() {
        // Assemble
        val remoteIssues = emptyList<Issue>()
        val dbTickets = listOf(
                Mocks.createTicket(),
                Mocks.createTicket(),
                Mocks.createTicket()
        )
        doReturn(Single.just(jiraClient)).whenever(jiraClientProvider).clientStream()
        doReturn(Observable.just(remoteIssues)).whenever(jiraTicketSearch).searchIssues(any(), any())
        doReturn(dbTickets).whenever(databaseRepository).loadTickets()

        // Act
        val result = ticketsInteractor.searchRemoteTickets(now = TimeMachine.now())
                .test()

        // Assert
        result.assertNoErrors()
        result.assertValueCount(1)
        val resultItems = result.onNextEvents.first()
        assertThat(resultItems.size).isEqualTo(3)

        verify(databaseRepository, times(0)).insertOrUpdate(any())
        verify(databaseRepository).loadTickets()
    }

}