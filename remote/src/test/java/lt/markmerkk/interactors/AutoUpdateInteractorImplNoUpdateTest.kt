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
class AutoUpdateInteractorImplNoUpdateTest {

    val settings: UserSettings = mock()
    val interactor = AutoUpdateInteractorImpl(userSettings = settings)

    @Before
    fun setUp() {
        whenever(settings.autoUpdateMinutes).thenReturn(-1)
    }

    @Test
    fun neverUpdated_noUpdate() {
        // Arrange
        whenever(settings.lastUpdate).thenReturn(-1)

        // Act
        val result = interactor.isAutoUpdateTimeoutHit(1000L)

        // Assert
        assertFalse(result)
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
    fun updateWayMore_noUpdate() {
        // Arrange
        val lastUpdate = 1000L
        val current = lastUpdate + (50 * 1000 * 60) // 50 minutes
        interactor.notifyUpdateComplete(lastUpdate)


        // Act
        val result = interactor.isAutoUpdateTimeoutHit(current)

        // Assert
        assertFalse(result)
    }
}