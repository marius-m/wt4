package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class DurationGoalForecasterTimeGoalTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val durationGoalForecaster = DurationGoalForecaster()

    @Test
    fun mon() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(3)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }
}