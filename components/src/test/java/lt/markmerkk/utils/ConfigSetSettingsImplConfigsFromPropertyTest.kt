package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.mock
import lt.markmerkk.ConfigPathProvider
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-18
 */
class ConfigSetSettingsImplConfigsFromPropertyTest {
    val pathProvider: ConfigPathProvider = mock()

    @Test
    fun emptyProperty_emptyList() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("")

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun one_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("property1")

        // Assert
        assertEquals(1, result.size)
        assertEquals("property1", result[0])
    }

    @Test
    fun many_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("property1,property2,property3")

        // Assert
        assertEquals(3, result.size)
        assertEquals("property1", result[0])
        assertEquals("property2", result[1])
        assertEquals("property3", result[2])
    }

    @Test
    fun many_emptyItem_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("property1,property2,,property3") // contains empty item

        // Assert
        assertEquals(3, result.size)
        assertEquals("property1", result[0])
        assertEquals("property2", result[1])
        assertEquals("property3", result[2])
    }

    @Test
    fun many_hasEmptySpace_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("property1,property2    ,property3") // contains empty space

        // Assert
        assertEquals(3, result.size)
        assertEquals("property1", result[0])
        assertEquals("property2", result[1])
        assertEquals("property3", result[2])
    }

    @Test
    fun many_breakPoints1_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("property1,property2,property3    ,\n")

        // Assert
        assertEquals(3, result.size)
        assertEquals("property1", result[0])
        assertEquals("property2", result[1])
        assertEquals("property3", result[2])
    }

    @Test
    fun many_breakPoints2_valid() {
        // Arrange
        val settings = ConfigSetSettingsImpl(pathProvider)

        // Act
        val result = settings.configsFromProperty("property1,property2,property3    ,\r\n")

        // Assert
        assertEquals(3, result.size)
        assertEquals("property1", result[0])
        assertEquals("property2", result[1])
        assertEquals("property3", result[2])
    }

}