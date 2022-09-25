package lt.markmerkk.validators

import lt.markmerkk.TimeMachine
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.TimeGap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimeGapFromTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun valid() {
        // Act
        val resultTimeGap = TimeGap.from(
                start = timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0),
                end = timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(10)
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(10)
        )
    }

    @Test
    fun equal() {
        // Act
        val resultTimeGap = TimeGap.from(
                start = timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0),
                end = timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
    }

    @Test
    fun endBeforeStart() {
        // Act
        val resultTimeGap = TimeGap.from(
                start = timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0),
                end = timeProvider.now().withHourOfDay(9)
                        .withMinuteOfHour(0)
        )

        // Assert
        assertThat(resultTimeGap.start).isEqualTo(
                timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultTimeGap.end).isEqualTo(
                timeProvider.now().withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
    }
}