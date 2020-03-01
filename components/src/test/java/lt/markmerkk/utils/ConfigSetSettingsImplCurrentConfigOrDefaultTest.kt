package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.mock
import lt.markmerkk.ConfigPathProvider
import org.junit.Assert.*
import org.junit.Test

class ConfigSetSettingsImplCurrentConfigOrDefaultTest {

    val pathProvider: ConfigPathProvider = mock()
    val settings = ConfigSetSettingsImpl(pathProvider)

    @Test
    fun valid() {
        // Arrange
        // Act
        settings.changeActiveConfig("valid_config")
        val result = settings.currentConfigOrDefault()

        // Assert
        assertEquals("valid_config", result)
    }

    @Test
    fun emptyConfig() {
        // Arrange
        // Act
        settings.changeActiveConfig("")
        val result = settings.currentConfigOrDefault()

        // Assert
        assertEquals(ConfigSetSettingsImpl.DEFAULT_ROOT_CONFIG_NAME, result)
    }

    @Test
    fun defaultConfig() {
        // Arrange
        // Act
        settings.changeActiveConfig(ConfigSetSettingsImpl.DEFAULT_ROOT_CONFIG_NAME)
        val result = settings.currentConfigOrDefault()

        // Assert
        assertEquals(ConfigSetSettingsImpl.DEFAULT_ROOT_CONFIG_NAME, result)
    }
}