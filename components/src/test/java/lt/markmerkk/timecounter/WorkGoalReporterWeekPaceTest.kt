package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.WorkGoalReporterStringResTest
import lt.markmerkk.entities.DateRange
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class WorkGoalReporterWeekPaceTest {

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
    fun mon_pacePositive() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: +15m")
    }

    @Test
    fun mon_paceEquals() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: +0m")
    }

    @Test
    fun mon_paceNegative() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(35)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: -2h 50m")
    }

    @Test
    fun tue_pacePositive() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: +15m")
    }

    @Test
    fun tue_paceEquals() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: +0m")
    }

    @Test
    fun tue_paceNegative() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
            .plusMinutes(35)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: -2h 50m")
    }

    @Test
    fun tue_pacePositive_displayDiffNextWeek() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate.plusDays(7))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }

    @Test
    fun tue_pacePositive_displayDiffPrevWeek() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate.minusDays(2))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }

    @Test
    fun tue_pacePositive_displayFirstDay() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate.minusDays(1))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: +15m")
    }

    @Test
    fun tue_pacePositive_displayLastDay() {
        // Assemble
        val nowDate = now.plusDays(5).toLocalDate() // tue
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)
            .plus(Duration.standardMinutes(45))

        // Act
        val result = workGoalReporter.reportPaceWeek(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Week pace: +15m")
    }
}