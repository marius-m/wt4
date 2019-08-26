package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class TicketLoaderLoadProjectCodesTest {

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
        doReturn(TimeMachine.now()).whenever(timeProvider).now()
    }

    @Test
    fun valid() {
        // Assemble
        val tickets = listOf(
                Mocks.createTicket(code = "TTS-111"),
                Mocks.createTicket(code = "TTS-222")
        )
        doReturn(Single.just(tickets))
                .whenever(ticketStorage).loadTickets()

        // Act
        loader.loadProjectCodes()

        // Assert
        verify(listener).onProjectCodes(
                listOf(
                        TicketLoader.ProjectCode(""),
                        TicketLoader.ProjectCode("TTS")
                )
        )
    }

    @Test
    fun differentProjects() {
        // Assemble
        val tickets = listOf(
                Mocks.createTicket(code = "TTS-111"),
                Mocks.createTicket(code = "TTS-222"),
                Mocks.createTicket(code = "WT-222")
        )
        doReturn(Single.just(tickets))
                .whenever(ticketStorage).loadTickets()

        // Act
        loader.loadProjectCodes()

        // Assert
        verify(listener).onProjectCodes(
                listOf(
                        TicketLoader.ProjectCode(""),
                        TicketLoader.ProjectCode("TTS"),
                        TicketLoader.ProjectCode("WT")
                )
        )
    }

    @Test
    fun noProjects() {
        // Assemble
        val tickets = emptyList<Ticket>()
        doReturn(Single.just(tickets))
                .whenever(ticketStorage).loadTickets()

        // Act
        loader.loadProjectCodes()

        // Assert
        verify(listener).onProjectCodes(
                listOf(
                        TicketLoader.ProjectCode("")
                )
        )
    }

    @Test
    fun error() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(ticketStorage).loadTickets()

        // Act
        loader.loadProjectCodes()

        // Assert
        verify(listener).onProjectCodes(
                listOf(
                        TicketLoader.ProjectCode("")
                )
        )
    }
}