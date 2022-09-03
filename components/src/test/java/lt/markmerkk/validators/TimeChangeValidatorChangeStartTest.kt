package lt.markmerkk.validators

import lt.markmerkk.TimeMachine
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeChangeValidatorChangeStartTest {

    private val timeProvider = TimeProviderTest()
    private val validator = TimeChangeValidator

    @Test
    fun simple() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(10)

        // Act
        val resultTimeGap = validator.changeStart(start, end)

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

    @Test
    fun equal() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)

        // Act
        val resultTimeGap = validator.changeStart(start, end)

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
    }

    @Test
    fun startIsAfterEnd() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(10)

        // Act
        val resultTimeGap = validator.changeStart(start, end)

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
    }
}