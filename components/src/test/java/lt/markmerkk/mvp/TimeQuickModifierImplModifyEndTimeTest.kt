package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TimeQuickModifierImplModifyEndTimeTest : AbsTimeQuickModifierImplTest() {

    @Test
    fun valid_subtractMinute() {
        // Assemble
        val currentDay = LocalDate.of(2001, 12, 10)
        val startTime = LocalTime.of(21, 30, 30)
        val endTime = LocalTime.of(21, 45, 30)

        // Act
        modifier.subtractEndTime(
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
                        endTime.minusMinutes(1)
                )
        )
    }

    @Test
    fun ignore_equalTime_subtractMinute() {
        // Assemble
        val currentDay = LocalDate.of(2001, 12, 10)
        val startTime = LocalTime.of(21, 30, 30)
        val endTime = LocalTime.of(21, 30, 30)

        // Act
        modifier.subtractEndTime(
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

    @Test
    fun valid_appendMinute() {
        // Assemble
        val currentDay = LocalDate.of(2001, 12, 10)
        val startTime = LocalTime.of(21, 30, 30)
        val endTime = LocalTime.of(21, 45, 30)

        // Act
        modifier.appendEndTime(
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
                        endTime.plusMinutes(1)
                )
        )
    }

}