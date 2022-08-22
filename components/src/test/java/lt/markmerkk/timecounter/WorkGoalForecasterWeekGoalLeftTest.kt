package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkGoalForecasterWeekGoalLeftTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workGoalForecaster = WorkGoalForecaster()

    @Test
    fun hasTimeLeft() {
        // Assemble
        val durationWorked = Duration.standardHours(38)

        // Act
        val result = workGoalForecaster
            .weekGoalLeft(
                durationWorked = durationWorked,
            )

        // Assert
        val expectDuration = Duration.standardHours(2)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun noTimeLeft() {
        // Assemble
        val durationWorked = Duration.standardHours(40)

        // Act
        val result = workGoalForecaster
            .weekGoalLeft(
                durationWorked = durationWorked,
            )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun workedMoreThanGoal() {
        // Assemble
        val durationWorked = Duration.standardHours(42)

        // Act
        val result = workGoalForecaster
            .weekGoalLeft(
                durationWorked = durationWorked,
            )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }
}