package lt.markmerkk.timecounter

import org.assertj.core.api.Assertions
import org.junit.Test

class WorkDaysInitTest {

    @Test
    fun valid() {
        // Assemble
        val inputDays = listOf(
            WorkDayRule.defaultWithWeekDay(WeekDay.MON),
            WorkDayRule.defaultWithWeekDay(WeekDay.TUE),
            WorkDayRule.defaultWithWeekDay(WeekDay.WED),
            WorkDayRule.defaultWithWeekDay(WeekDay.THU),
            WorkDayRule.defaultWithWeekDay(WeekDay.FRI),
            WorkDayRule.defaultWithWeekDay(WeekDay.SAT),
            WorkDayRule.defaultWithWeekDay(WeekDay.SUN),
        )

        // Act
        val result = WorkDays.withWorkDays(workDays = inputDays)

        // Assert
        Assertions.assertThat(result.workDayRules).isEqualTo(inputDays)
    }

    @Test(expected = IllegalArgumentException::class)
    fun missingDay() {
        // Assemble
        val inputDays = listOf(
            WorkDayRule.defaultWithWeekDay(WeekDay.MON),
            WorkDayRule.defaultWithWeekDay(WeekDay.TUE),
            WorkDayRule.defaultWithWeekDay(WeekDay.WED),
            WorkDayRule.defaultWithWeekDay(WeekDay.THU),
            // WorkDayRule.defaultWithWeekDay(WeekDay.FRIDAY),
            WorkDayRule.defaultWithWeekDay(WeekDay.SAT),
            WorkDayRule.defaultWithWeekDay(WeekDay.SUN),
        )

        // Act
        // Assert
        WorkDays.withWorkDays(workDays = inputDays)
    }
}