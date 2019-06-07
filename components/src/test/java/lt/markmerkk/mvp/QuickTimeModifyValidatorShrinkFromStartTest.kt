package lt.markmerkk.mvp

import lt.markmerkk.TimeMachine
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QuickTimeModifyValidatorShrinkFromStartTest {

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
        val resultTimeGap = validator.shrinkFromStart(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 5
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(5)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

    @Test
    fun shrinkMoreThanEnd() {
        // Assemble
        val start = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(10)

        // Act
        val resultTimeGap = validator.shrinkFromStart(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 20
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(9)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

}