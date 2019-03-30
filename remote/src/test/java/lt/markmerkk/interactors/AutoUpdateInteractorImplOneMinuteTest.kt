package lt.markmerkk.interactors

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.UserSettings
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-13
 */
class AutoUpdateInteractorImplOneMinuteTest {

    val settings: UserSettings = mock()
    val interactor = AutoUpdateInteractorImpl(userSettings = settings)

    @Before
    fun setUp() {
        whenever(settings.autoUpdateMinutes).thenReturn(1)
    }

    @Test
    fun neverUpdated_triggerUpdate() {
        // Arrange
        whenever(settings.lastUpdate).thenReturn(-1)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(1000L)

        // Assert
        assertTrue(result)
    }

    @Test
    fun lastUpdateCurrent_noTrigger() {
        // Arrange
        interactor.notifyUpdateComplete(1000L)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(1000L)

        // Assert
        assertFalse(result)
    }

    @Test
    fun lastUpdateLonger_triggerUpdate() {
        // Arrange
        val lastUpdate = 1000L
        val current = lastUpdate + (1 * 1000 * 60)
        interactor.notifyUpdateComplete(lastUpdate)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(current)

        // Assert
        assertTrue(result)
    }

    @Test
    fun lastUpdateWayMoreLonger_triggerUpdate() {
        // Arrange
        val lastUpdate = 1000L
        val current = lastUpdate + (10 * 1000 * 60) // 10 minutes
        interactor.notifyUpdateComplete(lastUpdate)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(current)

        // Assert
        assertTrue(result)
    }
}