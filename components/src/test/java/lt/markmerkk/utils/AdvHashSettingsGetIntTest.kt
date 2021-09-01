package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.Config
import lt.markmerkk.ConfigPathProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.Properties

class AdvHashSettingsGetIntTest {

    @Mock lateinit var configPathProvider: ConfigPathProvider
    @Mock lateinit var configSetSettings: ConfigSetSettings
    private lateinit var config: Config

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        doNothing().whenever(configSetSettings).load()
        doNothing().whenever(configSetSettings).save()
        doReturn("/").whenever(configSetSettings)
                .currentConfig()
        config = Config(
            debug = false,
            appName = "wt4",
            appFlavor = "basic",
            versionName = "test_version",
            versionCode = 1,
            cpp = configPathProvider,
            configSetSettings = configSetSettings,
            gaKey = "test"
        )
    }

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