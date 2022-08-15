package lt.markmerkk.timecounter

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.LocalTimeGap
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class WorkDayRuleDurationTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()

    @Test
    fun basic() {
        // Assemble
        val start = now.plusHours(9).toLocalTime()
        val end = now.plusHours(17).toLocalTime()
        val workDayRule = WorkDayRule(
            weekDay = WeekDay.MON,
            workSchedule = LocalTimeGap.from(start, end),
            timeBreak = TimeBreak.asEmpty(),
        )

        // Act
        val result = workDayRule.workDuration

        // Assert
        Assertions.assertThat(result)
            .isEqualTo(Duration.standardHours(8))
    }

    @Test
    fun basic_withBreak() {
        // Assemble
        val start = now.plusHours(8).toLocalTime()
        val end = now.plusHours(17).toLocalTime()
        val workDayRule = WorkDayRule(
            weekDay = WeekDay.MON,
            workSchedule = LocalTimeGap.from(start, end),
            timeBreak = TimeBreak.asDefault(),
        )

        // Act
        val result = workDayRule.workDuration

        // Assert
        val expectDuration = Duration.standardHours(8)
        Assertions.assertThat(result)
            .isEqualTo(expectDuration)
    }

    @Test
    fun invalid_breakLongerThanWork() {
        // Assemble
        val start = now.plusHours(9).toLocalTime()
        val end = now.plusHours(11).toLocalTime()
        val workDayRule = WorkDayRule(
            weekDay = WeekDay.MON,
            workSchedule = LocalTimeGap.from(start, end),
            timeBreak = TimeBreak(
                LocalTimeGap.from(
                    start = LocalTime.MIDNIGHT.plusHours(9),
                    end = LocalTime.MIDNIGHT.plusHours(13),
                )
            ),
        )

        // Act
        val result = workDayRule.workDuration

        // Assert
        Assertions.assertThat(result)
            .isEqualTo(Duration.ZERO)
    }

    @Test
    fun invalid_endBeforeStart() {
        // Assemble
        val start = now.plusHours(9).toLocalTime()
        val end = now.plusHours(6).toLocalTime()
        val workDayRule = WorkDayRule(
            weekDay = WeekDay.MON,
            workSchedule = LocalTimeGap.from(start, end),
            timeBreak = TimeBreak.asEmpty(),
        )

        // Act
        val result = workDayRule.workDuration

        // Assert
        Assertions.assertThat(result)
            .isEqualTo(Duration.ZERO)
    }
}