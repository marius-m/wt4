package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkDayRuleDurationWithTargetStartTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()

    @Test
    fun noBreak_basic() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(15)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.standardHours(2)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun noBreak_hasMinutes() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(30)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.standardHours(1)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun halfAfternoonBreak() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(12)
            .plusMinutes(30)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime
        )

        // Assert
        val expectDuration = Duration.standardHours(4)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun withAfternoon() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(9)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.standardHours(7)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun fullWorkDay() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(8)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun targetLessThanWorkSchduledStart() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(6)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.standardHours(8)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun invalid_targetAfterEnd() {
        // Assemble
        val workDayRule = WorkDayRule.default()
        val targetTime = LocalTime.MIDNIGHT
            .plusHours(20)

        // Act
        val result = workDayRule.workDurationWithTargetStart(
            targetStartTime = targetTime,
        )

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }
}