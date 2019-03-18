package lt.markmerkk.db2

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.*
import lt.markmerkk.tickets.TicketsNetworkRepo
import lt.markmerkk.tickets.TicketsRepository
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single

class TicketsRepositoryTicketsTest {

    @Mock lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Mock lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Mock lateinit var userSettings: UserSettings
    @Mock lateinit var timeProvider: TimeProvider
    lateinit var ticketsRepository: TicketsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketsRepository = TicketsRepository(
                ticketsDatabaseRepo,
                ticketsNetworkRepo,
                userSettings,
                timeProvider
        )
        doReturn(TimeMachine.now()).whenever(timeProvider).now()
    }

    @Test
    fun neverFetched() {
        // Assemble
        val now = DateTime.now().plusDays(10)
        doReturn(-1L).whenever(userSettings).ticketLastUpdate
        doReturn(Single.just(listOf(Mocks.createTicket())))
                .whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        doReturn(now).whenever(timeProvider).now()

        // Act
        val resultTickets = ticketsRepository.tickets(
                ticketRefreshTimeoutInDays = 1
        ).test()

        // Assert
        resultTickets.assertNoErrors()
        resultTickets.assertValueCount(1)
        verify(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        verify(userSettings).ticketLastUpdate = now.millis
    }

    @Test
    fun networkAlreadyFreshEnough() {
        // Assemble
        doReturn(TimeMachine.now().plusDays(2).millis).whenever(userSettings).ticketLastUpdate
        doReturn(Single.just(listOf(Mocks.createTicket())))
                .whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        doReturn(listOf(Mocks.createTicket()))
                .whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        val resultTickets = ticketsRepository.tickets(
                ticketRefreshTimeoutInDays = 1
        ).test()

        // Assert
        resultTickets.assertNoErrors()
        resultTickets.assertValueCount(1)
        verify(ticketsNetworkRepo, never()).searchRemoteTicketsAndCache(any())
        verify(userSettings, never()).ticketLastUpdate = any()
    }
}