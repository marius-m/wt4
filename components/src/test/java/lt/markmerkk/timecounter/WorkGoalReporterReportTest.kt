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
    fun valid_logged() {
        // Assemble
        val durationLogged = Duration.standardHours(2)
            .plus(Duration.standardMinutes(31))

        // Act
        val result = workGoalReporter.reportLoggedTotal(durationLogged)

        // Assert
        Assertions.assertThat(result).isEqualTo("Total: 2h 31m")
    }

    @Test
    fun valid_loggedOngoing() {
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
    fun valid_pacePositive() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Pace: +15m")
    }

    @Test
    fun valid_paceNegative() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(35)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Pace: -2h 50m")
    }
}