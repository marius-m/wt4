package lt.markmerkk.mvp

import lt.markmerkk.TimeMachine
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QuickTimeModifyValidatorMoveBackwardTest {

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
        val resultTimeGap = validator.moveBackward(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 60
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(10)
        )
    }


    @Test
    fun simple2() {
        // Assemble
        val start = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = TimeMachine.now()
                .withHourOfDay(10)
                .withMinuteOfHour(10)

        // Act
        val resultTimeGap = validator.moveBackward(
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
                        .withMinuteOfHour(40)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(9)
                        .withMinuteOfHour(50)
        )
    }

}