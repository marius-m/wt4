package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.Mocks
import lt.markmerkk.TimeMachine
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Ticket
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketsNetworkRepo
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers

class TicketLoaderLoadTest {

    @Mock lateinit var listener: TicketLoader.Listener
    @Mock lateinit var timeProvider: TimeProvider
    @Mock lateinit var ticketsRepository: TicketsRepository
    lateinit var loader: TicketLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = TicketLoader(
                listener,
                ticketsRepository,
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
                .whenever(ticketsRepository).tickets(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onTicketsReady(any())
    }

    @Test
    fun noTickets() {
        // Assemble
        doReturn(Single.just(emptyList<Ticket>()))
                .whenever(ticketsRepository).tickets(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onNoTickets()
    }

    @Test
    fun ticketFailure() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(ticketsRepository).tickets(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onError(any())
    }
}