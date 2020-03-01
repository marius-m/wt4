package lt.markmerkk

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.utils.ConfigSetSettings
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ConfigCfgPathTest {

    @Mock lateinit var pathProvider: ConfigPathProvider
    @Mock lateinit var configSetSettings: ConfigSetSettings

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun releasePath_returnDefault() {
        // Arrange
        doReturn("home_dir").whenever(pathProvider).userHome()
        whenever(pathProvider.configDefault())
                .thenReturn("wt4")
        whenever(configSetSettings.currentConfig())
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
        val result = config.profilePath()

        // Assert
        assertEquals("home_dir/.wt4/", result)
    }

    @Test
    fun debugPath_returnDefault() {
        // Arrange
        doReturn("home_dir").whenever(pathProvider).userHome()
        whenever(pathProvider.configDefault())
                .thenReturn("wt4_debug")
        whenever(configSetSettings.currentConfig())
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
        val result = config.profilePath()

        // Assert
        assertEquals("home_dir/.wt4_debug/", result)
    }

    @Test
    fun withExtension_returnExtension() {
        // Arrange
        doReturn("home_dir").whenever(pathProvider).userHome()
        whenever(pathProvider.configDefault())
                .thenReturn("wt4_debug")
        whenever(configSetSettings.currentConfig())
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
        val result = config.profilePath()

        // Assert
        assertEquals("home_dir/.wt4_debug/test_extension/", result)
    }

    @Test
    fun validPath() {
        // Arrange
        doReturn("home_dir").whenever(pathProvider).userHome()
        doReturn("wt4").whenever(pathProvider).configDefault()
        doReturn("text_extension").whenever(configSetSettings).currentConfig()
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