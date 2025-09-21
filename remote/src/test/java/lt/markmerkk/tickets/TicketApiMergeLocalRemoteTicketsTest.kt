package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.verification.VerificationMode

class TicketApiMergeLocalRemoteTicketsTest {

    @Mock lateinit var jiraClientProvider: JiraClientProvider
    @Mock lateinit var jiraTicketSearch: JiraTicketSearch
    @Mock lateinit var ticketsDatabaseRepo: TicketStorage
    @Mock lateinit var userSettings: UserSettings
    private lateinit var ticketApi: TicketApi

    private val timeProvider: TimeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketApi = TicketApi(
                jiraClientProvider,
                jiraTicketSearch,
                ticketsDatabaseRepo,
                userSettings
        )
    }

    @Test
    fun noTickets() {
        // Act
        ticketApi.mergeLocalAndRemoteTickets(
                localTickets = emptyList(),
                remoteTickets = emptyList()
        )

        // Assert
        Mockito.verifyNoInteractions(ticketsDatabaseRepo)
    }

    @Test
    fun onlyRemoteTickets() {
        // Assemble
        val localTickets = listOf<Ticket>()
        val remoteTickets = listOf(
                Mocks.createTicket(code = "DEV-111", status = "To do"),
                Mocks.createTicket(code = "DEV-222", status = "To do")
        )

        // Act
        ticketApi.mergeLocalAndRemoteTickets(
                localTickets = localTickets,
                remoteTickets = remoteTickets
        )

        // Assert
        val ticketCapture = argumentCaptor<Ticket>()
        verify(ticketsDatabaseRepo, times(2)).insertOrUpdateSync(ticketCapture.capture())
        val resultTickets = ticketCapture.allValues
        assertThat(resultTickets)
                .containsExactly(
                        Mocks.createTicket(code = "DEV-111", status = "To do"),
                        Mocks.createTicket(code = "DEV-222", status = "To do")
                )
    }

    @Test
    fun localUpdate() {
        // Assemble
        val localTickets = listOf(
                Mocks.createTicket(code = "DEV-111", status = "To do"),
                Mocks.createTicket(code = "DEV-222", status = "To do")
        )
        val remoteTickets = listOf(
                Mocks.createTicket(code = "DEV-111", status = "To do"),
                Mocks.createTicket(code = "DEV-222", status = "Done") // new status
        )

        // Act
        ticketApi.mergeLocalAndRemoteTickets(
                localTickets = localTickets,
                remoteTickets = remoteTickets
        )

        // Assert
        val ticketCapture = argumentCaptor<Ticket>()
        verify(ticketsDatabaseRepo, times(2)).insertOrUpdateSync(ticketCapture.capture())
        val resultTickets = ticketCapture.allValues
        assertThat(resultTickets)
                .containsExactly(
                        Mocks.createTicket(code = "DEV-111", status = "To do"),
                        Mocks.createTicket(code = "DEV-222", status = "Done")
                )
    }

    @Test
    fun notAllLocalUpdated() {
        // Assemble
        val localTickets = listOf(
                Mocks.createTicket(code = "DEV-111", status = "To do"),
                Mocks.createTicket(code = "DEV-222", status = "To do"),
                Mocks.createTicket(code = "DEV-333", status = "To do") // no remote update
                )
        val remoteTickets = listOf(
                Mocks.createTicket(code = "DEV-111", status = "To do"),
                Mocks.createTicket(code = "DEV-222", status = "To do")
        )

        // Act
        ticketApi.mergeLocalAndRemoteTickets(
                localTickets = localTickets,
                remoteTickets = remoteTickets
        )

        // Assert
        val ticketCapture = argumentCaptor<Ticket>()
        verify(ticketsDatabaseRepo, times(3)).insertOrUpdateSync(ticketCapture.capture())
        val resultTickets = ticketCapture.allValues
        assertThat(resultTickets)
                .containsExactly(
                        Mocks.createTicket(code = "DEV-111", status = "To do"),
                        Mocks.createTicket(code = "DEV-222", status = "To do"),
                        Mocks.createTicket(code = "DEV-333", status = "") // Remove old status
                )
    }
}