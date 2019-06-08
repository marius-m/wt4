package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.verify
import lt.markmerkk.TimeMachine
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Test

class TimeQuickModifierImplModifyEndTimeTest : AbsTimeQuickModifierImplTest() {

    @Test
    fun valid_subtractMinute() {
        // Assemble
        val currentDay = LocalDate(2001, 12, 10)
        val startTime = LocalTime(21, 30, 30)
        val endTime = LocalTime(21, 45, 30)

        // Act
        modifier.subtractEndTime(
                TimeMachine.withDateTime(currentDay, startTime),
                TimeMachine.withDateTime(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                TimeMachine.withDateTime(
                        currentDay,
                        startTime
                ),
                TimeMachine.withDateTime(
                        currentDay,
                        endTime.minusMinutes(1)
                )
        )
    }

    @Test
    fun ignore_equalTime_subtractMinute() {
        // Assemble
        val currentDay = LocalDate(2001, 12, 10)
        val startTime = LocalTime(21, 30, 30)
        val endTime = LocalTime(21, 30, 30)

        // Act
        modifier.subtractEndTime(
                TimeMachine.withDateTime(currentDay, startTime),
                TimeMachine.withDateTime(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                TimeMachine.withDateTime(
                        currentDay,
                        startTime
                ),
                TimeMachine.withDateTime(
                        currentDay,
                        endTime
                )
        )
    }

    @Test
    fun valid_appendMinute() {
        // Assemble
        val currentDay = LocalDate(2001, 12, 10)
        val startTime = LocalTime(21, 30, 30)
        val endTime = LocalTime(21, 45, 30)

        // Act
        modifier.appendEndTime(
                TimeMachine.withDateTime(currentDay, startTime),
                TimeMachine.withDateTime(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                TimeMachine.withDateTime(
                        currentDay,
                        startTime
                ),
                TimeMachine.withDateTime(
                        currentDay,
                        endTime.plusMinutes(1)
                )
        )
    }

}