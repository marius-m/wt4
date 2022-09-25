package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.Strings
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LogFormattersFormatTimeTest {

    @Mock lateinit var stringRes: LogFormatters.StringRes
    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        doReturn("Tomorrow").whenever(stringRes).resTomorrow()
    }

    @Test
    fun sameDay() {
        // Assemble
        val now = timeProvider.now()
        val dtTarget = now.plusHours(12)
            .plusMinutes(30)

        // Act
        val result = LogFormatters.formatTime(stringRes, now, dtTarget)

        // Assert
        Assertions.assertThat(result).isEqualTo("12:30")
    }

    @Test
    fun nextDay() {
        // Assemble
        val now = timeProvider.now()
        val dtTarget = now
            .plusDays(1)
            .plusHours(12)
            .plusMinutes(30)

        // Act
        val result = LogFormatters.formatTime(stringRes, now, dtTarget)

        // Assert
        Assertions.assertThat(result).isEqualTo("Tomorrow 12:30")
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
        val result = LogFormatters.formatTime(stringRes, now, dtTarget)

        // Assert
        Assertions.assertThat(result).isEqualTo("1970-01-03 12:30")
    }
}