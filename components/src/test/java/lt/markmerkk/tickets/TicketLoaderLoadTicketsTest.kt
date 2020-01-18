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

class TicketLoaderLoadTicketsTest {

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
        doReturn(Single.just(MocksTickets.tickets))
                .whenever(ticketStorage).loadFilteredTickets(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onFoundTickets(any())
    }

    @Test
    fun valid_filterProjectCode() {
        // Assemble
        doReturn(Single.just(MocksTickets.ticketMix))
                .whenever(ticketStorage).loadFilteredTickets(any())

        // Act
        loader.loadTickets(projectCode = TicketLoader.ProjectCode("WT"))

        // Assert
        val expectTicketResult = MocksTickets.ticketMix
                .map { TicketLoader.TicketScore(it, 0) }
                .first()
        verify(listener).onFoundTickets(listOf(expectTicketResult))
    }

    @Test
    fun valid_withFilter() {
        // Assemble
        doReturn(Single.just(MocksTickets.tickets))
                .whenever(ticketStorage).loadFilteredTickets(any())

        // Act
        loader.loadTickets(inputFilter = "TTS-115")

        // Assert
        verify(listener).onFoundTickets(listOf(
                TicketLoader.TicketScore(
                        MocksTickets.tickets[4],
                        29
                )
        ))
    }

    @Test
    fun noTickets() {
        // Assemble
        doReturn(Single.just(emptyList<List<Ticket>>()))
                .whenever(ticketStorage).loadFilteredTickets(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onNoTickets()
    }

    @Test
    fun ticketFailure() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(ticketStorage).loadFilteredTickets(any())

        // Act
        loader.loadTickets()

        // Assert
        verify(listener).onError(any())
    }
}