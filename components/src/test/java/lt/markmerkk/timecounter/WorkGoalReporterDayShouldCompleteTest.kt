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

class WorkGoalReporterDayShouldCompleteTest {

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
    fun noBreak() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete day at: 16:30")
    }

    @Test
    fun hasBreak() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(14)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(5)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete day at: 17:00")
    }

    @Test
    fun noWorkDone() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(21)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(0)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete day at: Tomorrow 01:00")
    }

    @Test
    fun alreadyComplete() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate)
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(11)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("Should complete day at: Complete")
    }

    @Test
    fun diffDisplayDate() {
        // Assemble
        val nowDate = now.plusDays(4).toLocalDate() // mon
        val displayDateRange = DateRange.forActiveDay(nowDate.plusDays(1))
        val nowTime = LocalTime.MIDNIGHT
            .plusHours(11)
            .plusMinutes(30)
        val targetNow = nowDate.toDateTime(nowTime)
        val durationWorked = Duration.standardHours(3)

        // Act
        val result = workGoalReporter.reportDayShouldComplete(
            now = targetNow,
            displayDateRange = displayDateRange,
            durationWorked = durationWorked,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo("")
    }
}