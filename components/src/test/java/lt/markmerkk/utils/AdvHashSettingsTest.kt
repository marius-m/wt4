package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.mock
import lt.markmerkk.Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class AdvHashSettingsTest {

    val config = Config(
            gaKey = "test_key",
            configPathProvider = mock(),
            configSetSettings = mock()
    )

    @Test
    fun testValidInput() {
        // Arrange
        val settings = AdvHashSettings(config)
        val properties = Properties()

        // Act
        settings.set("valid_key", "valid_value")
        settings.set("valid_key1", "valid_value1")
        settings.set("valid_key2", "valid_value2")
        settings.onSave(properties)
        settings.save()

        // Assert
        assertThat(properties["valid_key"]).isEqualTo("dmFsaWRfdmFsdWU=")
        assertThat(properties["valid_key1"]).isEqualTo("dmFsaWRfdmFsdWUx")
        assertThat(properties["valid_key2"]).isEqualTo("dmFsaWRfdmFsdWUy")
    }

    @Test
    fun testValidOutput() {
        // Arrange
        val settings = AdvHashSettings(config)

        // Act
        val outputProperties = Properties()
        outputProperties.put("valid_key", "dmFsaWRfdmFsdWU=")
        outputProperties.put("valid_key1", "dmFsaWRfdmFsdWUx")
        outputProperties.put("valid_key2", "dmFsaWRfdmFsdWUy")
        settings.onLoad(outputProperties)

        // Assert
        assertThat(settings.get("valid_key")).isEqualTo("valid_value")
        assertThat(settings.get("valid_key1")).isEqualTo("valid_value1")
        assertThat(settings.get("valid_key2")).isEqualTo("valid_value2")
    }

    @Test
    fun testMalformOutput() {
        // Arrange
        val settings = AdvHashSettings(config)

        // Act
        val outputProperties = Properties()
        outputProperties.put("valid_key", "aaa") // Invalid, cant be decoded
        settings.onLoad(outputProperties)

        // Assert
        assertThat(settings.get("valid_key")).isNotNull()
        assertThat(settings.keyValues.size).isEqualTo(1)
    }

    @Test
    fun testEmptyOutput() {
        // Arrange
        val settings = AdvHashSettings(config)

        // Act
        val outputProperties = Properties()
        outputProperties.put("valid_key", "") // Invalid, cant be decoded
        settings.onLoad(outputProperties)

        // Assert
        assertThat(settings.get("valid_key")).isEmpty()
        assertThat(settings.keyValues.size).isZero()
    }

}