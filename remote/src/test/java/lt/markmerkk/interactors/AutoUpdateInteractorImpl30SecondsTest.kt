package lt.markmerkk.interactors

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.UserSettings
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-13
 */
class AutoUpdateInteractorImpl30SecondsTest {

    val settings: UserSettings = mock()
    val interactor = AutoUpdateInteractorImpl(userSettings = settings)

    @Before
    fun setUp() {
        whenever(settings.autoUpdateMinutes).thenReturn(30)
    }

    @Test
    fun neverUpdated_triggerUpdate() {
        // Arrange
        // Act
        val result = interactor.isAutoUpdateTimeoutHit(1000L)

        // Assert
        assertTrue(result)
    }

    @Test
    fun updateEquals_noUpdate() {
        // Arrange
        interactor.notifyUpdateComplete(1000L)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(1000L)

        // Assert
        assertFalse(result)
    }

    @Test
    fun updateBeforeTimeout_noUpdate() {
        // Arrange
        val lastUpdate = 1000L
        val current = lastUpdate + (10 * 1000 * 60) // 10 minutes
        interactor.notifyUpdateComplete(lastUpdate)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(current)

        // Assert
        assertFalse(result)
    }

    @Test
    fun updateEqualsTimeout_triggerUpdate() {
        // Arrange
        val lastUpdate = 1000L
        val current = lastUpdate + (30 * 1000 * 60) // 30 minutes
        interactor.notifyUpdateComplete(lastUpdate)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(current)

        // Assert
        assertTrue(result)
    }

    @Test
    fun updateMoreThanTimeout_triggerUpdate() {
        // Arrange
        val lastUpdate = 1000L
        val current = lastUpdate + (50 * 1000 * 60) // 50 minutes
        interactor.notifyUpdateComplete(lastUpdate)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(current)

        // Assert
        assertTrue(result)
    }
}