package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.verify
import lt.markmerkk.TimeMachine
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Test

class TimeQuickModifierImplModifyStartTimeTest : AbsTimeQuickModifierImplTest() {

    @Test
    fun valid_subtractMinute() {
        // Assemble
        val currentDay = LocalDate(2001, 12, 10)
        val startTime = LocalTime(21, 30, 30)
        val endTime = LocalTime(21, 45, 30)

        // Act
        modifier.subtractStartTime(
                TimeMachine.withDateTime(currentDay, startTime),
                TimeMachine.withDateTime(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                TimeMachine.withDateTime(
                        currentDay,
                        startTime.minusMinutes(1)
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
        modifier.appendStartTime(
                TimeMachine.withDateTime(currentDay, startTime),
                TimeMachine.withDateTime(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                TimeMachine.withDateTime(
                        currentDay,
                        startTime.plusMinutes(1)
                ),
                TimeMachine.withDateTime(
                        currentDay,
                        endTime
                )
        )
    }

    @Test
    fun ignore_equalTime_appendMinute() {
        // Assemble
        val currentDay = LocalDate(2001, 12, 10)
        val startTime = LocalTime(21, 30, 30)
        val endTime = LocalTime(21, 30, 30) // equal time

        // Act
        modifier.appendStartTime(
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

}