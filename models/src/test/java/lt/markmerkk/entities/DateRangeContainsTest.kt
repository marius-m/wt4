package lt.markmerkk.entities

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions
import org.junit.Test

class DateRangeContainsTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun day_same() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate()
        val dateRange = DateRange.forActiveDay(initDate)
        val targetDate = initDate

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(true)
    }

    @Test
    fun day_next() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate()
        val dateRange = DateRange.forActiveDay(initDate)
        val targetDate = initDate.plusDays(1)

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(false)
    }

    @Test
    fun day_prev() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate()
        val dateRange = DateRange.forActiveDay(initDate)
        val targetDate = initDate.minusDays(1)

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(false)
    }

    @Test
    fun week_same() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate().plusDays(5) // tue
        val dateRange = DateRange.forActiveWeek(initDate)
        val targetDate = initDate

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(true)
    }

    @Test
    fun week_nextDay() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate().plusDays(5) // tue
        val dateRange = DateRange.forActiveWeek(initDate)
        val targetDate = initDate.plusDays(1)

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(true)
    }

    @Test
    fun week_nextWeek() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate().plusDays(5) // tue
        val dateRange = DateRange.forActiveWeek(initDate)
        val targetDate = initDate.plusDays(6)

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(false)
    }

    @Test
    fun week_prevDay() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate().plusDays(5) // tue
        val dateRange = DateRange.forActiveWeek(initDate)
        val targetDate = initDate.minusDays(1)

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(true)
    }

    @Test
    fun week_prevWeek() {
        // Assemble
        val initDate = timeProvider.now().toLocalDate().plusDays(5) // tue
        val dateRange = DateRange.forActiveWeek(initDate)
        val targetDate = initDate.minusDays(2)

        // Act
        val result = dateRange.contains(targetDate)

        // Assert
        Assertions.assertThat(result).isEqualTo(false)
    }
}