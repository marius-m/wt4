package lt.markmerkk.timecounter

import org.assertj.core.api.Assertions
import org.junit.Test

class WeekDayCompareTest {

    @Test
    fun sameDays() {
        // Assemble
        // Act
        val result = WeekDay.MON.compareTo(WeekDay.MON)

        // Assert
        Assertions.assertThat(result).isEqualTo(0)
    }

    @Test
    fun less() {
        // Assemble
        // Act
        val result = WeekDay.MON.compareTo(WeekDay.TUE)

        // Assert
        Assertions.assertThat(result).isEqualTo(-1)
    }

    @Test
    fun more() {
        // Assemble
        // Act
        val result = WeekDay.TUE.compareTo(WeekDay.MON)

        // Assert
        Assertions.assertThat(result).isEqualTo(1)
    }

    @Test
    fun less_midWeek() {
        // Assemble
        // Act
        val result = WeekDay.MON.compareTo(WeekDay.FRI)

        // Assert
        Assertions.assertThat(result).isEqualTo(-4)
    }
}