package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.Mocks
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.entities.Ticket
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class TicketInfoLoaderTest {

    @Mock lateinit var listener: TicketInfoLoader.Listener
    @Mock lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    lateinit var ticketInfoLoader: TicketInfoLoader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketInfoLoader = TicketInfoLoader(
                listener,
                ticketsDatabaseRepo,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun valid() {
        // Assemble
        val tickets = listOf(Mocks.createTicket())
        doReturn(Single.just(tickets))
                .whenever(ticketsDatabaseRepo).findTicketsByCode(any())

        // Act
        ticketInfoLoader.findTicket("tts-222")

        // Assert
        verify(listener).onTicketFound(tickets[0])
    }

    @Test
    fun noInput() {
        // Assemble
        // Act
        ticketInfoLoader.findTicket("")

        // Assert
        verify(ticketsDatabaseRepo, never()).findTicketsByCode(any())
    }

    @Test
    fun malformedTicket() {
        // Assemble
        // Act
        ticketInfoLoader.findTicket("asdf")

        // Assert
        verify(ticketsDatabaseRepo, never()).findTicketsByCode(any())
    }

    @Test
    fun valid_manyTickets() {
        // Assemble
        val tickets = listOf(
                Mocks.createTicket(),
                Mocks.createTicket(),
                Mocks.createTicket()
        )
        doReturn(Single.just(tickets))
                .whenever(ticketsDatabaseRepo).findTicketsByCode(any())

        // Act
        ticketInfoLoader.findTicket("tts-222")

        // Assert
        verify(listener).onTicketFound(tickets[0])
    }

    @Test
    fun noTickets() {
        // Assemble
        val tickets = emptyList<Ticket>()
        doReturn(Single.just(tickets))
                .whenever(ticketsDatabaseRepo).findTicketsByCode(any())

        // Act
        ticketInfoLoader.findTicket("tts-222")

        // Assert
        verify(listener).onNoTicket()
    }

    @Test
    fun error() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(ticketsDatabaseRepo).findTicketsByCode(any())

        // Act
        ticketInfoLoader.findTicket("tts-222")

        // Assert
        verify(listener).onNoTicket()
    }
}