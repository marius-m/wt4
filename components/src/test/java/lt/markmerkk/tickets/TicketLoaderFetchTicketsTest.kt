package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.*
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
    @Mock lateinit var ticketStorage: TicketStorage
    @Mock lateinit var ticketApi: TicketApi
    @Mock lateinit var userSettings: UserSettings
    lateinit var loader: TicketLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = TicketLoader(
                listener,
                ticketStorage,
                ticketApi,
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
        doReturn(Single.just(newTickets)).whenever(ticketApi).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketStorage).loadTicketsWithEnabledStatuses()

        // Act
        loader.fetchTickets()

        // Assert
        verify(userSettings).ticketLastUpdate = TimeMachine.now().plusHours(1).millis
        verify(ticketApi).searchRemoteTicketsAndCache(any())
        verify(ticketStorage).loadTicketsWithEnabledStatuses()
        verify(listener).onFoundTickets(any())
    }

    @Test
    fun ticketsFreshEnough() {
        // Assemble
        val newTickets = listOf(Mocks.createTicket())
        doReturn(Single.just(newTickets)).whenever(ticketApi).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketStorage).loadTicketsWithEnabledStatuses()
        doReturn(TimeMachine.now()).whenever(timeProvider).now()

        // Act
        loader.fetchTickets()

        // Assert
        verify(userSettings, never()).ticketLastUpdate = any()
        verify(ticketApi, never()).searchRemoteTicketsAndCache(any())
        verify(ticketStorage).loadTicketsWithEnabledStatuses()
        verify(listener).onFoundTickets(any())
    }

    @Test
    fun ticketsFreshEnough_forceRefresh() {
        // Assemble
        val newTickets = listOf(Mocks.createTicket())
        doReturn(Single.just(newTickets)).whenever(ticketApi).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketStorage).loadTicketsWithEnabledStatuses()
        doReturn(TimeMachine.now()).whenever(timeProvider).now()

        // Act
        loader.fetchTickets(forceRefresh = true)

        // Assert
        verify(userSettings).ticketLastUpdate = any()
        verify(ticketApi).searchRemoteTicketsAndCache(any())
        verify(ticketStorage).loadTicketsWithEnabledStatuses()
        verify(listener).onFoundTickets(any())
    }

    @Test
    fun noTickets() {
        // Assemble
        val newTickets = emptyList<Ticket>()
        doReturn(Single.just(newTickets)).whenever(ticketApi).searchRemoteTicketsAndCache(any())
        doReturn(Single.just(newTickets)).whenever(ticketStorage).loadTicketsWithEnabledStatuses()

        // Act
        loader.fetchTickets()

        // Assert
        verify(userSettings).ticketLastUpdate = any()
        verify(ticketApi).searchRemoteTicketsAndCache(any())
        verify(listener).onNoTickets()
    }

    @Test
    fun errorFetching() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException())).whenever(ticketApi).searchRemoteTicketsAndCache(any())

        // Act
        loader.fetchTickets()

        // Assert
        verify(userSettings, never()).ticketLastUpdate = any()
        verify(ticketApi).searchRemoteTicketsAndCache(any())
        verify(ticketStorage, never()).loadTickets()
    }

}