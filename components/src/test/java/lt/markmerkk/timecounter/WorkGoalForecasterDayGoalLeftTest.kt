package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkGoalForecasterDayGoalLeftTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workGoalForecaster = WorkGoalForecaster()

    @Test
    fun hasTimeLeft() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(6)

        // Act
        val result = workGoalForecaster
            .dayGoalLeft(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDuration = Duration.standardHours(2)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun noTimeLeft() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(8)

        // Act
        val result = workGoalForecaster
            .dayGoalLeft(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun workedMoreThanGoal() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(11)

        // Act
        val result = workGoalForecaster
            .dayGoalLeft(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }
}