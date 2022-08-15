package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Assert.*
import org.junit.Test

class WorkDayRuleDurationWithTargetEndTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()

    @Test
    fun basic() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)

        // Act
        val result = workDayRule.durationWithTargetEnd(
            targetTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.standardHours(2)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun onlyHourPassed() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(9)
            .plusMinutes(0)

        // Act
        val result = workDayRule.durationWithTargetEnd(
            targetTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun invalid_targetBeforeStart() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(5)
            .plusMinutes(30)

        // Act
        val result = workDayRule.durationWithTargetEnd(
            targetTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }
}