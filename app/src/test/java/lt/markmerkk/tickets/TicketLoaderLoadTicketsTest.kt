package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.*
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
        doReturn(TimeMachine.now()).whenever(timeProvider).now()
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Single.just(listOf(Mocks.createTicket())))
                .whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onTicketsAvailable(any())
    }

    @Test
    fun noTickets() {
        // Assemble
        doReturn(Single.just(emptyList<List<Ticket>>()))
                .whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onNoTickets()
    }

    @Test
    fun ticketFailure() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onError(any())
    }
}