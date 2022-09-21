package lt.markmerkk.utils

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.junit.Assert.*
import org.junit.Test

class LogFormattersFormatTimeTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun sameDay() {
        // Assemble
        val now = timeProvider.now()
        val dtTarget = now.plusHours(12)
            .plusMinutes(30)

        // Act
        val result = LogFormatters.formatTime(now, dtTarget)

        // Assert
        Assertions.assertThat(result).isEqualTo("12:30")
    }

    @Test
    fun differentDay() {
        // Assemble
        val now = timeProvider.now()
        val dtTarget = now
            .plusDays(2)
            .plusHours(12)
            .plusMinutes(30)

        // Act
        val result = LogFormatters.formatTime(now, dtTarget)

        // Assert
        Assertions.assertThat(result).isEqualTo("1970-01-03 12:30")
    }
}