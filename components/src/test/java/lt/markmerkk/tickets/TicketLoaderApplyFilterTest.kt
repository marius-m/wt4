package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
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
    @Mock lateinit var ticketStorage: TicketStorage
    @Mock lateinit var ticketApi: TicketApi
    @Mock lateinit var userSettings: UserSettings
    lateinit var loader: TicketLoader

    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loader = TicketLoader(
                listener,
                ticketStorage,
                ticketApi,
                timeProvider,
                userSettings,
                testScheduler,
                testScheduler
        )
        doReturn(Single.just(MocksTickets.tickets)).whenever(ticketStorage).loadTickets()
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        loader.onAttach()
        loader.loadTickets()
        reset(listener)

        loader.applyFilter(inputFilter = "TTS-115")
        testScheduler.advanceTimeBy(TicketLoader.FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS)

        // Assert
        verify(listener).onTicketsAvailable(listOf(MocksTickets.tickets[4])) // only TTS-005
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