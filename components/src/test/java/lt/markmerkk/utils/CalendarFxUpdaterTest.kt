package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class CalendarFxUpdaterTest {

    @Mock lateinit var listener: CalendarFxUpdater.Listener
    lateinit var updater: CalendarFxUpdater

    val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        updater = CalendarFxUpdater(
                listener,
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
        verify(listener).onCurrentTimeUpdate(any())
    }

    @Test
    fun valid_many() {
        // Assemble
        // Act
        updater.onAttach()
        testScheduler.advanceTimeBy(3, TimeUnit.MINUTES)

        // Assert
        verify(listener, times(6)).onCurrentTimeUpdate(any())
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
        verify(listener).onCurrentTimeUpdate(any())
    }

}