package lt.markmerkk.utils

import java.util.Properties
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.*

/**
 * Created by mariusmerkevicius on 12/21/15.
 */
class AdvHashSettingsTest {
    @Test
    fun testValidInput() {
        // Arrange
        val settings = AdvHashSettings()
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
        val settings = AdvHashSettings()

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
        val settings = AdvHashSettings()

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
        val settings = AdvHashSettings()

        // Act
        val outputProperties = Properties()
        outputProperties.put("valid_key", "") // Invalid, cant be decoded
        settings.onLoad(outputProperties)

        // Assert
        assertThat(settings.get("valid_key")).isNull()
        assertThat(settings.keyValues.size).isZero()
    }

}