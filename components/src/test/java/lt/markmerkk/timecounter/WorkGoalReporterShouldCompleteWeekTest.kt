package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.WorkGoalReporterStringResTest
import lt.markmerkk.entities.DateRange
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class WorkGoalReporterShouldCompleteWeekTest {

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
    fun weekStart() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(6)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete week at: 17:00")
    }

    @Test
    fun workAlreadyComplete() {
        // Assemble
        val nowDate = now.plusDays(8).toLocalDate() // Fri
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(41)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete week at: Complete")
    }

    @Test
    fun endOfWeek() {
        // Assemble
        val nowDate = now.plusDays(8).toLocalDate() // fri
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(17)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(38)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete week at: 19:00")
    }

    @Test
    fun endOfWeek_lotsOfTimeMissing() {
        // Assemble
        val nowDate = now.plusDays(8).toLocalDate() // fri
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(17)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(20)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete week at: Tomorrow 13:00")
    }

    @Test
    fun endOfWeek_multiDaysOfTimeMissing() {
        // Assemble
        val nowDate = now.plusDays(8).toLocalDate() // fri
        val displayDateRange = DateRange.forActiveWeek(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(19)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(2)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete week at: 1970-01-11 07:00")
    }

    @Test
    fun displayPrevWeek() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveWeek(nowDate.minusDays(1))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(6)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }
    @Test
    fun displayNextWeek() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveWeek(nowDate.plusDays(7))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(15)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(6)

        // Act
        val result = workGoalReporter.reportWeekShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }
}