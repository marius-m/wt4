package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.junit.Test

class DurationGoalForecasterDayGoalTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val durationGoalForecaster = DurationGoalForecaster()

    @Test
    fun mon() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForDay(targetDate = targetDate)

        // Assert
        val expectDurationGoal = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun tue() {
        // Assemble
        val targetDate = now.plusDays(5).toLocalDate() // tue

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForDay(targetDate = targetDate)

        // Assert
        val expectDurationGoal = Duration.standardHours(16)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun wholeWeek() {
        // Assemble
        val targetDate = now.plusDays(10).toLocalDate() // sun

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForDay(targetDate = targetDate)

        // Assert
        val expectDurationGoal = Duration.standardHours(40)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }
}