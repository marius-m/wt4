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
    fun mon_noBreak() {
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

    @Test
    fun mon_withAfternoonBreak() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(14)
            .plusMinutes(30)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(5)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun mon_wholeDay() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // mon
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(19)
            .plusMinutes(30)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun tue_noBreak() {
        // Assemble
        val targetDate = now.plusDays(5).toLocalDate() // tue
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(10)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(10)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun tue_fullDay() {
        // Assemble
        val targetDate = now.plusDays(5).toLocalDate() // tue
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(17)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(16)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun fullWorkWeek() {
        // Assemble
        val targetDate = now.plusDays(8).toLocalDate() // fri
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(17)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(40)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }

    @Test
    fun fullWeek() {
        // Assemble
        val targetDate = now.plusDays(10).toLocalDate() // sun
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(17)

        // Act
        val result = durationGoalForecaster
            .forecastDurationGoalForTime(targetDate = targetDate, targetTime = targetTime)

        // Assert
        val expectDurationGoal = Duration.standardHours(40)
        Assertions.assertThat(result).isEqualTo(expectDurationGoal)
    }
}