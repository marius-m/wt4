package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkGoalForecasterDayDurationForTimeTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workGoalForecaster = WorkGoalForecaster()

    @Test
    fun mon_noBreak() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForTargetTime(
                targetDate = targetDate,
                targetTime = targetTime,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(3)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun mon_withBreak() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(30)

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForTargetTime(
                targetDate = targetDate,
                targetTime = targetTime,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(6)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun fri_noBreak() {
        // Assemble
        val targetDate = now.plusDays(8).toLocalDate() // fri
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForTargetTime(
                targetDate = targetDate,
                targetTime = targetTime,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(3)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun fri_withBreak() {
        // Assemble
        val targetDate = now.plusDays(8).toLocalDate() // fri
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(30)

        // Act
        val result = workGoalForecaster
            .forecastDayDurationGoalForTargetTime(
                targetDate = targetDate,
                targetTime = targetTime,
            )

        // Assert
        val expectDurationGoal = Duration.standardHours(6)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }
}