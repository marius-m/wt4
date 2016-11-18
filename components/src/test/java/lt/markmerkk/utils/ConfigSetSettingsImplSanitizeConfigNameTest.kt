package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.mock
import lt.markmerkk.ConfigPathProvider
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-19
 */
class ConfigSetSettingsImplSanitizeConfigNameTest {
    val pathProvider: ConfigPathProvider = mock()

    @Test
    fun emptyList_emptyConfig() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("")

        // Assert
        assertEquals("", result)
    }

    @Test
    fun default_emptyConfig() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("default")

        // Assert
        assertEquals("", result)
    }

    @Test
    fun valid_properConfig() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("config_1")

        // Assert
        assertEquals("config_1", result)
    }

    @Test
    fun valid_invalidCharacterSeparator_properConfig() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("config_1,") // Contains invalid char

        // Assert
        assertEquals("config_1", result)
    }

    @Test
    fun valid_emptySpaces_properConfig() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("   config_1    ") // Contains empty spaces

        // Assert
        assertEquals("config_1", result)
    }

    // List manipulation

    @Test
    fun newConfig_addNewOne() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("config_1")

        // Assert
        assertEquals(1, settings.configs.size)
        assertEquals("config_1", settings.configs[0])
    }

    @Test
    fun configExist_noCreation() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)
        settings.configs += "config_1" // already exist

        // Act
        val result = settings.sanitizeConfigName("config_1")

        // Assert
        assertEquals(1, settings.configs.size)
        assertEquals("config_1", settings.configs[0])
    }

    @Test
    fun empty_noCreation() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.sanitizeConfigName("")

        // Assert
        assertEquals(0, settings.configs.size)
    }

}