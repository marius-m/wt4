package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.WorkGoalReporterStringResTest
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class WorkGoalReporterReportTest {

    private lateinit var workGoalForecaster: WorkGoalForecaster
    private lateinit var workGoalReporter: WorkGoalReporter
    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        workGoalForecaster = WorkGoalForecaster()
        workGoalReporter = WorkGoalReporter(
            workGoalForecaster = workGoalForecaster,
            stringRes = WorkGoalReporterStringResTest(),
        )
    }

    @Test
    fun logged() {
        // Assemble
        val durationLogged = Duration.standardHours(2)
            .plus(Duration.standardMinutes(31))

        // Act
        val result = workGoalReporter.reportLogged(durationLogged)

        // Assert
        Assertions.assertThat(result).isEqualTo("Total: 2h 31m")
    }

    @Test
    fun loggedOngoing() {
        // Assemble
        val durationLogged = Duration.standardHours(2)
            .plus(Duration.standardMinutes(31))
        val durationOngoing = Duration.standardMinutes(20)

        // Act
        val result = workGoalReporter.reportLoggedWithOngoing(durationLogged, durationOngoing)

        // Assert
        Assertions.assertThat(result).isEqualTo("Total: 2h 31m + 20m = 2h 51m")
    }

    @Test
    fun shouldCompleteDay_noBreak() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete: 16:30")
    }

    @Test
    fun shouldCompleteDay_hasBreak() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(14)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(5)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete: 17:00")
    }

    @Test
    fun dayDuration() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(14)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(5)

        // Act
        val result = workGoalReporter.reportDayGoalDuration(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Day goal: 8h (3h left)")
    }

    @Test
    fun dayDuration_noTimeLeft() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(14)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(8)

        // Act
        val result = workGoalReporter.reportDayGoalDuration(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Day goal: 8h (0m left)")
    }

    @Test
    fun daySchedule() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(14)
        val targetNow = nowDate.toDateTime(nowTime)

        // Act
        val result = workGoalReporter.reportDaySchedule(
            now = targetNow,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Day schedule: MON 08:00 - 17:00 (Break: 12:00 - 13:00)")
    }

    @Test
    fun shouldCompleteWeek_weekStart() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(6)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete: 17:00")
    }

    @Test
    fun shouldCompleteWeek_endOfWeek() {
        // Assemble
        val nowDate = now.plusDays(8).toLocalDate() // fri
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(17)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(38)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete: 19:00")
    }

    @Test
    fun shouldCompleteWeek_endOfWeek_lotsOfTimeMissing() {
        // Assemble
        val nowDate = now.plusDays(8).toLocalDate() // fri
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(17)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(20)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete: 1970-01-10 13:00")
    }
}