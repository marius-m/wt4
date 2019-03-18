package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.mock
import lt.markmerkk.Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class AdvHashSettingsGetIntTest {

    private val config = Config(
            gaKey = "test_key",
            configPathProvider = mock(),
            configSetSettings = mock()
    )

    @Test
    fun stringOutput() {
        // Arrange
        val settings = AdvHashSettings(config)
        val outputProperties = Properties()
        outputProperties.put("valid_key", "dmFsaWRfdmFsdWU=")

        // Act
        settings.onLoad(outputProperties)
        val resultValue = settings.getInt(key = "valid_key", defaultValue = -1)

        // Assert
        assertThat(resultValue).isEqualTo(-1)
    }

    @Test
    fun validOutput() {
        // Arrange
        val settings = AdvHashSettings(config)
        val outputProperties = Properties()
        outputProperties.put("valid_key", "MTA=")

        // Act
        settings.onLoad(outputProperties)
        val resultValue = settings.getInt(key = "valid_key", defaultValue = -1)

        // Assert
        assertThat(resultValue).isEqualTo(10)
    }

    @Test
    fun validInput() {
        // Arrange
        val settings = AdvHashSettings(config)
        val properties = Properties()

        // Act
        settings.set("valid_key", 10.toString())
        settings.onSave(properties)
        settings.save()

        // Assert
        assertThat(properties["valid_key"]).isEqualTo("MTA=")
    }

}