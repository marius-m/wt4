package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkGoalForecasterShouldFinishDayTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workGoalForecaster = WorkGoalForecaster()

    @Test
    fun mon_workedAsNeeded_workLeft_hasBreakIncluded() {
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
            .forecastShouldFinishDay(
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
    fun mon_workedLess_workLeft_hasBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(5)
            .plus(Duration.standardMinutes(15))

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(45)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedWayLess_workLeft_hasBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(2)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(21)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_notWorked_workLeft_hasBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(21)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.ZERO

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(5)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(1)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedMore_workLeft_hasBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(7)
            .plus(Duration.standardMinutes(15))

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(45)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedWayMore_workLeft_hasBreak() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(14)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(15)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedAsNeeded_hasBreak_workFinished() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(17)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(8)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
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
    fun mon_workedAsNeeded_workFinished_diffCurrentTime() {
        // Assemble
        val localNow = now.plusDays(4) // mon
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(18)
                    .plusMinutes(0)
            )
        val durationWorked = Duration.standardHours(8)

        // Act
        val result = workGoalForecaster
            .forecastShouldFinishDay(
                dtCurrent = localNow,
                durationWorked = durationWorked,
            )

        // Assert
        val expectDtFinish = now.plusDays(4)
            .withTime(
                LocalTime.MIDNIGHT
                    .plusHours(18)
                    .plusMinutes(0)
            )
        Assertions.assertThat(result).isEqualTo(expectDtFinish)
    }

    @Test
    fun mon_workedAsNeeded_workLeft_noBreak() {
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
            .forecastShouldFinishDay(
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
    fun mon_workedAsNeeded_workLeft_middleOfBreak() {
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
            .forecastShouldFinishDay(
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
}