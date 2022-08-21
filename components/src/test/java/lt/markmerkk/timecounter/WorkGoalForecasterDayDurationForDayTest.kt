package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkGoalForecasterDayDurationForDayTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workGoalForecaster = WorkGoalForecaster()

    @Test
    fun mon_noBreak() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForWholeDay(
                targetDate = targetDate,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun mon_withBreak() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForWholeDay(
                targetDate = targetDate,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun fri_noBreak() {
        // Assemble
        val targetDate = now.plusDays(8).toLocalDate() // fri

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForWholeDay(
                targetDate = targetDate,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun sat_withBreak() {
        // Assemble
        val targetDate = now.plusDays(9).toLocalDate() // sat

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForWholeDay(
                targetDate = targetDate,
            )

        // Assert
        val expectDurationGoal = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }
}