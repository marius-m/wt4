package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.WorkGoalReporterStringResTest
import lt.markmerkk.entities.DateRange
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.LocalTime
import org.joda.time.Period
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class WorkGoalReporterDayPaceTest {

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
    fun pacePositive() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Pace: +15m")
    }

    @Test
    fun paceEquals() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Pace: +0m")
    }

    @Test
    fun paceNegative() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(35)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Pace: -2h 50m")
    }

    @Test
    fun diffDisplayDate() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate.plusDays(5))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }

    @Test
    fun nextDisplayDate() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate.plusDays(1))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }

    @Test
    fun prevDisplayDate() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate.minusDays(1))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceDay(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }
}