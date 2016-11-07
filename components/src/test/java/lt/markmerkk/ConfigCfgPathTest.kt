package lt.markmerkk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.utils.ConfigSetSettings
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-07
 */
class ConfigCfgPathTest {

    val pathProvider: ConfigPathProvider = mock()
    val configSetSettings: ConfigSetSettings = mock()

    @Before
    fun setUp() {
        whenever(pathProvider.userHome())
                .thenReturn("home_dir")
    }

    @Test
    fun releasePath_returnDefault() {
        // Arrange
        whenever(pathProvider.configDefault())
                .thenReturn("wt4")
        whenever(configSetSettings.configSetName)
                .thenReturn("")
        val config = Config(
                debug = false,
                versionName = "test_version",
                versionCode = 1,
                gaKey = "test_key",
                configPathProvider = pathProvider,
                configSetSettings = configSetSettings
        )

        // Act
        val result = config.generateRelativePath()

        // Assert
        assertEquals("home_dir/.wt4/", result)
    }

    @Test
    fun debugPath_returnDefault() {
        // Arrange
        whenever(pathProvider.configDefault())
                .thenReturn("wt4_debug")
        whenever(configSetSettings.configSetName)
                .thenReturn("")
        val config = Config(
                debug = false,
                versionName = "test_version",
                versionCode = 1,
                gaKey = "test_key",
                configPathProvider = pathProvider,
                configSetSettings = configSetSettings
        )

        // Act
        val result = config.generateRelativePath()

        // Assert
        assertEquals("home_dir/.wt4_debug/", result)
    }

    @Test
    fun withExtension_returnExtension() {
        // Arrange
        whenever(pathProvider.configDefault())
                .thenReturn("wt4_debug")
        whenever(configSetSettings.configSetName)
                .thenReturn("test_extension")
        val config = Config(
                debug = false,
                versionName = "test_version",
                versionCode = 1,
                gaKey = "test_key",
                configPathProvider = pathProvider,
                configSetSettings = configSetSettings
        )

        // Act
        val result = config.generateRelativePath()

        // Assert
        assertEquals("home_dir/.wt4_debug/test_extension/", result)
    }

    @Test
    fun validPath_triggerAbsolutePath() {
        // Arrange
        whenever(pathProvider.configDefault())
                .thenReturn("wt4")
        whenever(configSetSettings.configSetName)
                .thenReturn("test_extension")
        val config = Config(
                debug = false,
                versionName = "test_version",
                versionCode = 1,
                gaKey = "test_key",
                configPathProvider = pathProvider,
                configSetSettings = configSetSettings
        )

        // Act
        config.cfgPath

        // Assert
        verify(pathProvider).absolutePathWithMissingFolderCreate(any())
    }

}