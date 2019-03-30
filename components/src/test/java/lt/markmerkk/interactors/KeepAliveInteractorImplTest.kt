package lt.markmerkk.interactors

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-12
 */
class KeepAliveInteractorImplTest {

    val listener: KeepAliveInteractor.Listener = mock()
    val testScheduler = Schedulers.test()
    val interactor = KeepAliveInteractorImpl(
            testScheduler,
            testScheduler
    )

    @Before
    fun setUp() {
        interactor.register(listener)
    }

    @After
    fun tearDown() {
        interactor.unregister(listener)
    }

    @Test
    fun onAttach_triggerUpdate() {
        // Arrange
        // Act
        interactor.onAttach()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // Assert
        verify(listener).update()
    }

    @Test
    fun afterMinute_triggerUpdate() {
        // Arrange
        // Act
        interactor.onAttach()
        testScheduler.advanceTimeBy(1, TimeUnit.MINUTES)

        // Assert
        verify(listener, times(2)).update()
    }

    @Test
    fun afterDetach_noTrigger() {
        // Arrange
        // Act
        interactor.onAttach()
        testScheduler.advanceTimeBy(30, TimeUnit.SECONDS)
        interactor.onDetach()
        testScheduler.advanceTimeBy(5, TimeUnit.MINUTES)

        // Assert
        verify(listener, times(1)).update()
    }

}