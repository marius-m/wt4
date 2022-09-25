package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkGoalForecasterShouldFinishWeekTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workGoalForecaster = WorkGoalForecaster()

    @Test
    fun mon_workLeft_hasBreakIncluded() {
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
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun tue_workFinished() {
        // Assemble
        val localNow = now.plusDays(5) // tue
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(16)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(5)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun tue_workedAsNeeded() {
        // Assemble
        val localNow = now.plusDays(5) // tue
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(14)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(5)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun tue_workedABitLess() {
        // Assemble
        val localNow = now.plusDays(5) // tue
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(12)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(5)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(19)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun tue_workedWayMore() {
        // Assemble
        val localNow = now.plusDays(5) // tue
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(20)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(5)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedAsNeeded_noBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(11)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(3)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(16)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedAsNeeded_middleOfBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(12)
                    .plusMinutes(30)
            )
        val durationWorked = Duration.standardHours(4)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(16)
                    .plusMinutes(30)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun fri_endOfDay_workedAsNeeded() {
        // Assemble
        val localNow = now.plusDays(8) // fri
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(40)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(8)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun fri_endOfDay_missingWork() {
        // Assemble
        val localNow = now.plusDays(8) // fri
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(38)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(8)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(19)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun fri_endOfDay_lotsOfMissingWork() {
        // Assemble
        val localNow = now.plusDays(8) // fri
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(20)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(9) // next day
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(13)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun fri_workDayLeft_alreadyFinished() {
        // Assemble
        val localNow = now.plusDays(8) // fri
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
            )
        val durationWorked = Duration.standardHours(40)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(8)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun fri_workDayLeft_early_alreadyFinished() {
        // Assemble
        val localNow = now.plusDays(8) // fri
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(10)
            )
        val durationWorked = Duration.standardHours(40)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishWeek(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(8)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(10)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }
}