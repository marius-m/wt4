package lt.markmerkk.timecounter

import lt.markmerkk.MocksComponents
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.utils.hourglass.HourGlass
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class WorkGoalDurationCalculatorDurationRunningClockTest {

    @Mock lateinit var hourGlass: HourGlass

    private lateinit var workGoalDurationCalculator: WorkGoalDurationCalculator

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        workGoalDurationCalculator = WorkGoalDurationCalculator(
            hourGlass = hourGlass,
        )
    }

    @Test
    fun notRunning() {
        // Assemble
        val now = timeProvider.now()
        val displayDateStart = now.toLocalDate()
        val displayDateEnd = displayDateStart.plusDays(1)
        val hourGlass = MocksComponents.createHourGlass(
            timeProvider = timeProvider,
            isRunning = false,
        )

        // Act
        val result = workGoalDurationCalculator.durationRunningClock(
            hourGlass = hourGlass,
            displayDateStart = displayDateStart,
            displayDateEnd = displayDateEnd,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(Duration.ZERO)
    }

    @Test
    fun runningDuration() {
        // Assemble
        val now = timeProvider.now()
        val displayDateStart = now.toLocalDate()
        val displayDateEnd = displayDateStart.plusDays(1)
        val hourGlass = MocksComponents.createHourGlass(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(10),
            isRunning = true
        )

        // Act
        val result = workGoalDurationCalculator.durationRunningClock(
            hourGlass = hourGlass,
            displayDateStart = displayDateStart,
            displayDateEnd = displayDateEnd,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(Duration.standardMinutes(10))
    }

    @Test
    fun running_displayDateBeforeCurrent() {
        // Assemble
        val now = timeProvider.now()
        val displayDateStart = now.minusDays(2).toLocalDate()
        val displayDateEnd = displayDateStart.plusDays(1)
        val hourGlass = MocksComponents.createHourGlass(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(10),
            isRunning = true,
        )

        // Act
        val result = workGoalDurationCalculator.durationRunningClock(
            hourGlass = hourGlass,
            displayDateStart = displayDateStart,
            displayDateEnd = displayDateEnd,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(Duration.ZERO)
    }

    @Test
    fun running_displayDateAfterCurrent() {
        // Assemble
        val now = timeProvider.now()
        val displayDateStart = now.plusDays(2).toLocalDate()
        val displayDateEnd = displayDateStart.plusDays(1)
        val hourGlass = MocksComponents.createHourGlass(
            timeProvider = timeProvider,
            start = now,
            end = now.plusMinutes(10),
            isRunning = true,
        )

        // Act
        val result = workGoalDurationCalculator.durationRunningClock(
            hourGlass = hourGlass,
            displayDateStart = displayDateStart,
            displayDateEnd = displayDateEnd,
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(Duration.ZERO)
    }
}