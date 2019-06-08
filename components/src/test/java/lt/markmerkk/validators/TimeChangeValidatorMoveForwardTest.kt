package lt.markmerkk.validators

import lt.markmerkk.TimeMachine
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeChangeValidatorMoveForwardTest {

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
        val resultTimeGap = validator.moveForward(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 60
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(11)
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
        val resultTimeGap = validator.moveForward(
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
                        .withMinuteOfHour(20)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(30)
        )
    }

}