package lt.markmerkk.tickets

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.Mocks
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class TicketLoaderApplyFilterTest {

    @Mock lateinit var listener: TicketLoader.Listener
    @Mock lateinit var timeProvider: TimeProvider
    @Mock lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Mock lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Mock lateinit var userSettings: UserSettings
    lateinit var loader: TicketLoader

    private val testScheduler = TestScheduler()
    private val tickets: List<Ticket> = listOf(
            "google",           // TTS-001
            "bing",             // TTS-002
            "facebook",         // TTS-003
            "linkedin",         // TTS-004
            "twitter",          // TTS-005
            "googleplus",       // TTS-006
            "bingnews",         // TTS-007
            "plexoogl"          // TTS-008
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
                testScheduler,
                testScheduler
        )
        doReturn(Single.just(tickets)).whenever(ticketsDatabaseRepo).loadTickets()
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        loader.onAttach()
        loader.loadTickets()
        reset(listener)

        loader.applyFilter(inputFilter = "TTS-005")
        testScheduler.advanceTimeBy(TicketLoader.FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS)

        // Assert
        verify(listener).onTicketsAvailable(listOf(tickets[4])) // only TTS-005
    }

    @Test
    fun tooLittleTimePass() {
        // Assemble
        // Act
        loader.onAttach()
        loader.loadTickets()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        reset(listener)

        loader.applyFilter(inputFilter = "TTS-005")
        testScheduler.advanceTimeBy(TicketLoader.FILTER_INPUT_THROTTLE_MILLIS - 200, TimeUnit.MILLISECONDS)

        // Assert
        verifyZeroInteractions(listener)
    }

}