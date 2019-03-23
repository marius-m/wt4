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

    private val tickets: List<Ticket> = listOf(
            "google",           // TTS-1
            "bing",             // TTS-2
            "facebook",         // TTS-3
            "linkedin",         // TTS-4
            "twitter",          // TTS-5
            "googleplus",       // TTS-6
            "bingnews",         // TTS-7
            "plexoogl"          // TTS-8
    ).mapIndexed { index: Int, description: String ->
        Mocks.createTicket(code = "TTS-00${index + 1}", description = description)
    }

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
        doReturn(Single.just(tickets))
                .whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onTicketsAvailable(any())
    }

    @Test
    fun valid_withFilter() {
        // Assemble
        doReturn(Single.just(tickets))
                .whenever(ticketsDatabaseRepo).loadTickets()

        // Act
        loader.loadTickets(inputFilter = "TTS-005")

        // Assert
        verify(listener).onTicketsAvailable(listOf(tickets[4]))
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