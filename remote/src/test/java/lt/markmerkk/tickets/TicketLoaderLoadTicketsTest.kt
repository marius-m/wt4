package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.Mocks
import lt.markmerkk.TimeMachine
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Ticket
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class TicketLoaderLoadTicketsTest {

    @Mock lateinit var listener: TicketLoader.Listener
    @Mock lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Mock lateinit var timeProvider: TimeProvider
    lateinit var loader: TicketLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = TicketLoader(
                listener,
                ticketsNetworkRepo,
                timeProvider,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
        doReturn(TimeMachine.now()).whenever(timeProvider).now()
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Single.just(listOf(Mocks.createTicket())))
                .whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onTicketsReady(any())
    }

    @Test
    fun noTickets() {
        // Assemble
        doReturn(Single.just(emptyList<Ticket>()))
                .whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onNoTickets()
    }

    @Test
    fun ticketFailure() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onError(any())
    }
}