package lt.markmerkk.utils

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTimeUtils
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class LogUtilsFormatLogToTextTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now().plusHours(3)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        DateTimeUtils.setCurrentMillisFixed(1L)
    }

    @Test
    fun valid() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(5),
            code = "DEV-123",
            comment = "valid_comment",
        )

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("DEV-123 (03:00 - 03:05 = 5m) valid_comment")
    }

    @Test
    fun noTask() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(5),
            code = "",
            comment = "valid_comment",
        )

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("(03:00 - 03:05 = 5m) valid_comment")
    }

    @Test
    fun noComment() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(5),
            code = "DEV-123",
            comment = "",
        )

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("DEV-123 (03:00 - 03:05 = 5m)")
    }

    @Test
    fun noTaskNoComment() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(5),
            code = "",
            comment = "",
        )

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("(03:00 - 03:05 = 5m)")
    }
}