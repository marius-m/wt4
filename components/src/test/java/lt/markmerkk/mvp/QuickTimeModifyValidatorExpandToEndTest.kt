package lt.markmerkk.mvp

import lt.markmerkk.TimeMachine
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QuickTimeModifyValidatorExpandToEndTest {

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
        val resultTimeGap = validator.expandToEnd(
                timeGap = TimeGap.from(
                        start,
                        end
                ),
                minutes = 10
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(20)
        )
    }

}