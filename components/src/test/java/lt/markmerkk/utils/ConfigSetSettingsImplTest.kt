package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.mock
import lt.markmerkk.ConfigPathProvider
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ConfigSetSettingsImplTest {

    private val pathProvider: ConfigPathProvider = mock()

    @Test
    fun noValue_returnEmpty() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val properties = Properties()
        settings.onLoad(properties)

        // Assert
        assertEquals("", settings.currentConfig())
    }

    @Test
    fun wrongValue_returnEmpty() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val properties = Properties()
        properties.put("wrong_key", "valid_value")
        settings.onLoad(properties)

        // Assert
        assertEquals("", settings.currentConfig())
    }

    @Test
    fun rightValue_returnValid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val properties = Properties()
        properties.put(ConfigSetSettingsImpl.KEY_CONFIG_NAME, "valid_value")
        settings.onLoad(properties)

        // Assert
        assertEquals("valid_value", settings.currentConfig())
    }

    @Test
    fun validValue_triggerSave() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val initProperties = Properties()
        initProperties.put(ConfigSetSettingsImpl.KEY_CONFIG_NAME, "valid_value")
        settings.onLoad(initProperties)
        val resultProperties = Properties()
        settings.onSave(resultProperties)

        // Assert
        assertEquals("valid_value", resultProperties.get(ConfigSetSettingsImpl.KEY_CONFIG_NAME))
    }

}