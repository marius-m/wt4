package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class WorkDaysTargetDaysByDateTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()
    private val workDays = WorkDays.asDefault()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun valid_mon() {
        // Assemble
        val targetDate = now.plusDays(4).toLocalDate() // monday

        // Act
        val result = workDays.spawnTargetDaysByDate(targetDate = targetDate)

        // Assert
        Assertions.assertThat(result).containsExactly(
            WorkDayRule.defaultWithWeekDay(WeekDay.MON),
        )
    }

    @Test
    fun valid_wed() {
        // Assemble
        val targetDate = now.plusDays(6).toLocalDate() // wed

        // Act
        val result = workDays.spawnTargetDaysByDate(targetDate = targetDate)

        // Assert
        Assertions.assertThat(result).containsExactly(
            WorkDayRule.defaultWithWeekDay(WeekDay.MON),
            WorkDayRule.defaultWithWeekDay(WeekDay.TUE),
            WorkDayRule.defaultWithWeekDay(WeekDay.WED),
        )
    }

    @Test
    fun valid_sun() {
        // Assemble
        val targetDate = now.plusDays(10).toLocalDate() // sun

        // Act
        val result = workDays.spawnTargetDaysByDate(targetDate = targetDate)

        // Assert
        Assertions.assertThat(result).containsExactly(
            WorkDayRule.defaultWithWeekDay(WeekDay.MON),
            WorkDayRule.defaultWithWeekDay(WeekDay.TUE),
            WorkDayRule.defaultWithWeekDay(WeekDay.WED),
            WorkDayRule.defaultWithWeekDay(WeekDay.THU),
            WorkDayRule.defaultWithWeekDay(WeekDay.FRI),
            WorkDayRule.defaultWithWeekDay(WeekDay.SAT),
            WorkDayRule.defaultWithWeekDay(WeekDay.SUN),
        )
    }
}