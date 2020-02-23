package lt.markmerkk.utils

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogFormattersHasMultipleDatesTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun noLogs() {
        // Act
        val result = LogFormatters.hasMultipleDates(logs = emptyList())

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun sameDate() {
        // Assemble
        val now = timeProvider.now()
        val logs = listOf(
                Mocks.createLog(timeProvider, start = now),
                Mocks.createLog(timeProvider, start = now)
        )

        // Act
        val result = LogFormatters.hasMultipleDates(logs = logs)

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun multipleDates() {
        // Assemble
        val now = timeProvider.now()
        val logs = listOf(
                Mocks.createLog(timeProvider, start = now),
                Mocks.createLog(timeProvider, start = now.plusDays(1))
        )

        // Act
        val result = LogFormatters.hasMultipleDates(logs = logs)

        // Assert
        assertThat(result).isTrue()
    }
}