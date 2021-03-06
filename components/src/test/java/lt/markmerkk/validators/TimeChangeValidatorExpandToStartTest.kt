package lt.markmerkk.validators

import lt.markmerkk.TimeMachine
import lt.markmerkk.entities.TimeGap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeChangeValidatorExpandToStartTest {

    private val validator = TimeChangeValidator

    @Test
    fun simple() {
        // Assemble
        val start = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = TimeMachine.now()
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
                TimeMachine.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(50)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

}