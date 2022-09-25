package lt.markmerkk.validators

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.TimeGap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeChangeValidatorExpandToStartTest {

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
        val resultTimeGap = validator.expandToStart(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 10
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(50)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

}