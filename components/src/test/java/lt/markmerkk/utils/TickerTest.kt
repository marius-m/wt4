package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventTickTock
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class TickerTest {

    @Mock lateinit var eventBus: WTEventBus
    lateinit var updater: Ticker

    val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        updater = Ticker(
                eventBus,
                testScheduler,
                testScheduler
        )
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        updater.onAttach()
        testScheduler.advanceTimeBy(30, TimeUnit.SECONDS)

        // Assert
        verify(eventBus).post(EventTickTock())
    }

    @Test
    fun valid_many() {
        // Assemble
        // Act
        updater.onAttach()
        testScheduler.advanceTimeBy(3, TimeUnit.MINUTES)

        // Assert
        verify(eventBus, times(6)).post(EventTickTock())
    }

    @Test
    fun unsubscribe() {
        // Assemble
        // Act
        updater.onAttach()
        testScheduler.advanceTimeBy(30, TimeUnit.SECONDS)
        updater.onDetach()
        testScheduler.advanceTimeBy(10L, TimeUnit.MINUTES)

        // Assert
        verify(eventBus).post(any())
    }

}