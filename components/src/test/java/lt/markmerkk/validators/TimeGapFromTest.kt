package lt.markmerkk.validators

import lt.markmerkk.TimeMachine
import lt.markmerkk.entities.TimeGap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeGapFromTest {

    @Test
    fun valid() {
        // Act
        val resultTimeGap = TimeGap.from(
                start = TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0),
                end = TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(10)
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

    @Test
    fun equal() {
        // Act
        val resultTimeGap = TimeGap.from(
                start = TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0),
                end = TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
    }

    @Test
    fun endBeforeStart() {
        // Act
        val resultTimeGap = TimeGap.from(
                start = TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0),
                end = TimeMachine.now().withHourOfDay(9)
                        .withMinuteOfHour(0)
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                TimeMachine.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
    }
}