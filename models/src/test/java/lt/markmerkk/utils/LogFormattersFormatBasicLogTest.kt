package lt.markmerkk.utils

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogFormattersFormatBasicLogTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun basic() {
        // act
        val result = LogFormatters.formatLogBasic(
                log = Mocks.createLog(
                        timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(30) ,
                        code = "WT-1",
                        comment = "1234"
                ),
                includeDate = false
        )

        // Assert
        assertThat(result).isEqualTo("00:00 - 00:30 (30m) >> 'WT-1' '1234'")
    }

    @Test
    fun noCode() {
        // act
        val result = LogFormatters.formatLogBasic(
                log = Mocks.createLog(
                        timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(30) ,
                        code = "",
                        comment = "1234"
                ),
                includeDate = false
        )

        // Assert
        assertThat(result).isEqualTo("00:00 - 00:30 (30m) >> '1234'")
    }

    @Test
    fun includeDate() {
        // act
        val result = LogFormatters.formatLogBasic(
                log = Mocks.createLog(
                        timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(30) ,
                        code = "",
                        comment = "1234"
                ),
                includeDate = true
        )

        // Assert
        assertThat(result).isEqualTo("1970-01-01 00:00 - 00:30 (30m) >> '1234'")
    }
}