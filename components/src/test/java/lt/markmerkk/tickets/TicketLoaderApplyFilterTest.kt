package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single
import rx.schedulers.TestScheduler
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import org.mockito.Mockito

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
        doReturn(Single.just(MocksTickets.tickets)).whenever(ticketStorage).loadFilteredTickets(userSettings)
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        loader.onAttach()
        loader.loadTickets()
        reset(listener)

        loader.changeFilterStream(Observable.just("TTS-115"))
        testScheduler.advanceTimeBy(TicketLoader.FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS)

        // Assert
        val expectTickets = listOf(TicketLoader.TicketScore(MocksTickets.tickets[4], 29))
        verify(listener).onFoundTickets(
                any(),
                any(),
                eq(expectTickets)
        ) // only TTS-005

    }

    @Test
    fun tooLittleTimePass() {
        // Assemble
        val publishSubject = PublishSubject.create<String>()

        // Act
        loader.changeFilterStream(publishSubject)
        testScheduler.triggerActions()
        publishSubject.onNext("TTS-005")
        testScheduler.advanceTimeBy(TicketLoader.FILTER_INPUT_THROTTLE_MILLIS - 200, TimeUnit.MILLISECONDS)
        Mockito.verifyNoInteractions(listener)

        // Act
        testScheduler.advanceTimeBy(TicketLoader.FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS)

        // Assert
        verify(listener).onFoundTickets(any(), any(), any())
    }

}