package lt.markmerkk.interactors

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.WorklogStorage
import lt.markmerkk.utils.tracker.ITracker
import org.junit.Test
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-13
 */
class KeepAliveGASessionImplTest {

    val worklogStorage: WorklogStorage = mock()
    val tracker: ITracker = mock()
    val logStorage = LogStorage(worklogStorage, TimeProviderTest())
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
        testScheduler.advanceTimeBy(10L, TimeUnit.MINUTES)

        // Assert
        verify(tracker).sendView(any())
    }

    @Test
    fun twoFullSessions_sendTwoEvents() {
        // Arrange
        // Act
        keepAliveSession.onAttach()
        testScheduler.advanceTimeBy(20L, TimeUnit.MINUTES)

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