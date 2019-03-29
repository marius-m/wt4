package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.mock
import lt.markmerkk.ConfigPathProvider
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-18
 */
class ConfigSetSettingsImplConfigsToPropertyTest {
    val pathProvider: ConfigPathProvider = mock()

    @Test
    fun emptyList_emptyOutput() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsToProperty(listOf())

        // Assert
        assertEquals("", result)
    }

    @Test
    fun oneValue_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsToProperty(listOf("property1"))

        // Assert
        assertEquals("property1", result)
    }

    @Test
    fun moreValues_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsToProperty(
                listOf("property1", "property2", "property3")
        )

        // Assert
        assertEquals("property1,property2,property3", result)
    }

    @Test
    fun moreValues_propertyWithSeparatorInside_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsToProperty(listOf(
                "property1",
                "property2,", // Invalid separator that breaks list
                "property3"
        ))

        // Assert
        assertEquals("property1,property2,property3", result)
    }

    @Test
    fun moreValues_emptyValue_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsToProperty(listOf(
                "property1",
                "", // Empty value should be ignored
                "property3"
        ))

        // Assert
        assertEquals("property1,property3", result)
    }

    @Test
    fun moreValues_containsEmptySpace_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsToProperty(listOf(
                "property1",
                "   property2    ",
                "property3"
        ))

        // Assert
        assertEquals("property1,property2,property3", result)
    }
}