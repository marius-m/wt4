package lt.markmerkk

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-07
 */
class ConfigCfgPathTest {



    @Test
    fun test_input_should() {
        // Arrange
        val pathProvider: ConfigPathProvider = mock()
        whenever(pathProvider.userHome())
                .thenReturn("home_dir")
        val config = Config(
                debug = false,
                versionName = "test_version",
                versionCode = 1,
                gaKey = "test_key",
                configPathProvider = pathProvider
        )

        // Act
        val result = config.cfgPath

        // Assert
        assertEquals("home_dir/wt4/", result)
    }
}