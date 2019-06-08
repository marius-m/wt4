package lt.markmerkk.validators

import lt.markmerkk.TimeMachine
import lt.markmerkk.validators.QuickTimeModifyValidator
import lt.markmerkk.validators.TimeGap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QuickTimeModifyValidatorShrinkFromEndTest {

    private val validator = QuickTimeModifyValidator

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
        val resultTimeGap = validator.shrinkFromEnd(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 1
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0))
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(9))
    }

    @Test
    fun shrinkMoreThanStart() {
        // Assemble
        val start = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(10)

        // Act
        val resultTimeGap = validator.shrinkFromEnd(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 20
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(50))
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(50))
    }

}