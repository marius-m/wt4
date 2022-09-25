package lt.markmerkk.validators

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeChangeValidatorChangeEndTest {

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
        val resultTimeGap = validator.changeEnd(start, end)

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
        val resultTimeGap = validator.changeEnd(start, end)

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
    fun endIsBeforeStart() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(9)
                .withMinuteOfHour(10)

        // Act
        val resultTimeGap = validator.changeEnd(start, end)

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(10)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(10)
        )
    }

}