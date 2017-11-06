package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TimeQuickModifierImplModifyStartTimeTest : AbsTimeQuickModifierImplTest() {

    @Test
    fun valid_subtractMinute() {
        // Assemble
        val currentDay = LocalDate.of(2001, 12, 10)
        val startTime = LocalTime.of(21, 30, 30)
        val endTime = LocalTime.of(21, 45, 30)

        // Act
        modifier.subtractStartTime(
                LocalDateTime.of(currentDay, startTime),
                LocalDateTime.of(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                LocalDateTime.of(
                        currentDay,
                        startTime.minusMinutes(1)
                ),
                LocalDateTime.of(
                        currentDay,
                        endTime
                )
        )
    }

    @Test
    fun valid_appendMinute() {
        // Assemble
        val currentDay = LocalDate.of(2001, 12, 10)
        val startTime = LocalTime.of(21, 30, 30)
        val endTime = LocalTime.of(21, 45, 30)

        // Act
        modifier.appendStartTime(
                LocalDateTime.of(currentDay, startTime),
                LocalDateTime.of(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                LocalDateTime.of(
                        currentDay,
                        startTime.plusMinutes(1)
                ),
                LocalDateTime.of(
                        currentDay,
                        endTime
                )
        )
    }

    @Test
    fun ignore_equalTime_appendMinute() {
        // Assemble
        val currentDay = LocalDate.of(2001, 12, 10)
        val startTime = LocalTime.of(21, 30, 30)
        val endTime = LocalTime.of(21, 30, 30) // equal time

        // Act
        modifier.appendStartTime(
                LocalDateTime.of(currentDay, startTime),
                LocalDateTime.of(currentDay, endTime)
        )

        // Assert
        verify(listener).onTimeModified(
                LocalDateTime.of(
                        currentDay,
                        startTime
                ),
                LocalDateTime.of(
                        currentDay,
                        endTime
                )
        )
    }

}