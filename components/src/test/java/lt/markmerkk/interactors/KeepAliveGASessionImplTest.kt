package lt.markmerkk.interactors

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.utils.tracker.ITracker
import org.junit.Assert.*
import org.junit.Test
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-13
 */
class KeepAliveGASessionImplTest {

    val tracker: ITracker = mock()
    val executor: IExecutor = mock()
    val logStorage = LogStorage(executor)
    val testScheduler = Schedulers.test()!!
    val keepAliveSession = KeepAliveGASessionImpl(
            logStorage,
            tracker,
            testScheduler
    )

    @Test
    fun onAttach_shouldNotSendInstantly() {
        // Arrange
        // Act
        keepAliveSession.onAttach()

        // Assert
        verify(tracker, never()).sendView(any())
    }

    @Test
    fun fewMinutesIn_shouldNotSendInstantly() {
        // Arrange
        // Act
        keepAliveSession.onAttach()
        testScheduler.advanceTimeBy(5, TimeUnit.MINUTES)

        // Assert
        verify(tracker, never()).sendView(any())
    }

    @Test
    fun fullSessionTimeout_sendOneEvent() {
        // Arrange
        // Act
        keepAliveSession.onAttach()
        testScheduler.advanceTimeBy(30, TimeUnit.MINUTES)

        // Assert
        verify(tracker).sendView(any())
    }

    @Test
    fun twoFullSessions_sendTwoEvents() {
        // Arrange
        // Act
        keepAliveSession.onAttach()
        testScheduler.advanceTimeBy(60, TimeUnit.MINUTES)

        // Assert
        verify(tracker, times(2)).sendView(any())
    }

    @Test
    fun attachDetach_noEvents() {
        // Arrange
        // Act
        keepAliveSession.onAttach()
        keepAliveSession.onDetach()
        testScheduler.advanceTimeBy(60, TimeUnit.MINUTES)

        // Assert
        verify(tracker, never()).sendView(any())
    }
}