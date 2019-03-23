package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class TicketLoaderFetchTicketsTest {

    @Mock lateinit var listener: TicketLoader.Listener
    @Mock lateinit var timeProvider: TimeProvider
    @Mock lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Mock lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Mock lateinit var userSettings: UserSettings
    lateinit var loader: TicketLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = TicketLoader(
                listener,
                ticketsDatabaseRepo,
                ticketsNetworkRepo,
                timeProvider,
                userSettings,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
        doReturn(TimeMachine.now().plusHours(1)).whenever(timeProvider).now()
        doReturn(TimeMachine.now().millis).whenever(userSettings).lastUpdate
    }

    @Test
    fun valid() {
        // Assemble
        val newTickets = listOf(Mocks.createTicket())
        doReturn(Single.just(newTickets)).whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketsDatabaseRepo).loadTickets()
        val loaderSpy = spy(loader)

        // Act
        loaderSpy.fetchTickets()

        // Assert
        verify(userSettings).ticketLastUpdate = TimeMachine.now().plusHours(1).millis
        verify(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        verify(listener).onNewTickets(any())
        verify(loaderSpy).loadTickets() // load tickets after fetch
    }

    @Test
    fun ticketsFreshEnough() {
        // Assemble
        val newTickets = listOf(Mocks.createTicket())
        doReturn(Single.just(newTickets)).whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketsDatabaseRepo).loadTickets()
        val loaderSpy = spy(loader)
        doReturn(TimeMachine.now()).whenever(timeProvider).now()

        // Act
        loaderSpy.fetchTickets()

        // Assert
        verify(userSettings, never()).ticketLastUpdate = any()
        verify(ticketsNetworkRepo, never()).searchRemoteTicketsAndCache(any())
        verify(listener, never()).onNewTickets(any())
        verify(loaderSpy, never()).loadTickets()
    }

    @Test
    fun ticketsFreshEnough_forceRefresh() {
        // Assemble
        val newTickets = listOf(Mocks.createTicket())
        doReturn(Single.just(newTickets)).whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketsDatabaseRepo).loadTickets()
        val loaderSpy = spy(loader)
        doReturn(TimeMachine.now()).whenever(timeProvider).now()

        // Act
        loaderSpy.fetchTickets(forceRefresh = true)

        // Assert
        verify(userSettings).ticketLastUpdate = any()
        verify(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        verify(listener).onNewTickets(any())
    }

    @Test
    fun noTickets() {
        // Assemble
        val newTickets = emptyList<Ticket>()
        doReturn(Single.just(newTickets)).whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        val loaderSpy = spy(loader)

        // Act
        loaderSpy.fetchTickets()

        // Assert
        verify(userSettings).ticketLastUpdate = any()
        verify(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        verify(listener, never()).onNewTickets(any())
        verify(loaderSpy, never()).loadTickets()
    }

    @Test
    fun errorFetching() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException())).whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        val loaderSpy = spy(loader)

        // Act
        loaderSpy.fetchTickets()

        // Assert
        verify(userSettings, never()).ticketLastUpdate = any()
        verify(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
        verify(listener, never()).onNewTickets(any())
        verify(loaderSpy, never()).loadTickets()
    }

}