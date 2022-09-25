package lt.markmerkk.entities

import org.assertj.core.api.Assertions
import org.joda.time.LocalTime
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class LocalTimeGapIsOverlappingTest {
    @Test
    fun noOverlap_equalsStart() {
        // Assemble
        val otherTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(9),
            end = LocalTime.MIDNIGHT.plusHours(10),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(10),
            end = LocalTime.MIDNIGHT.plusHours(11),
        )

        // Act
        val result = inputTimeGap.isOverlapping(otherTimeGap)

        // Assert
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun noOverlap_equalsEnd() {
        // Assemble
        val otherTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(9),
            end = LocalTime.MIDNIGHT.plusHours(10),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(9),
        )

        // Act
        val result = inputTimeGap.isOverlapping(otherTimeGap)

        // Assert
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun overlap_overlapStart() {
        // Assemble
        val otherTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(9),
            end = LocalTime.MIDNIGHT.plusHours(10),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(9).plusMinutes(30),
            end = LocalTime.MIDNIGHT.plusHours(15),
        )

        // Act
        val result = inputTimeGap.isOverlapping(otherTimeGap)

        // Assert
        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun overlap_overlapEnd() {
        // Assemble
        val otherTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(9),
            end = LocalTime.MIDNIGHT.plusHours(10),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(9).plusMinutes(30),
        )

        // Act
        val result = inputTimeGap.isOverlapping(otherTimeGap)

        // Assert
        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun many_noOverlap() {
        // Assemble
        val timeGaps = listOf(
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(10),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(11),
                end = LocalTime.MIDNIGHT.plusHours(12),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(15),
                end = LocalTime.MIDNIGHT.plusHours(16),
            ),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(10),
            end = LocalTime.MIDNIGHT.plusHours(11),
        )

        // Act
        val result = inputTimeGap.isOverlappingWithAny(timeGaps)

        // Assert
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun many_overlapEnd() {
        // Assemble
        val timeGaps = listOf(
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(10),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(11),
                end = LocalTime.MIDNIGHT.plusHours(12),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(15),
                end = LocalTime.MIDNIGHT.plusHours(16),
            ),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(9).plusMinutes(30),
        )

        // Act
        val result = inputTimeGap.isOverlappingWithAny(timeGaps)

        // Assert
        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun many_overlapStart() {
        // Assemble
        val timeGaps = listOf(
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(10),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(11),
                end = LocalTime.MIDNIGHT.plusHours(12),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(15),
                end = LocalTime.MIDNIGHT.plusHours(16),
            ),
        )
        val inputTimeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(15).plusMinutes(30),
            end = LocalTime.MIDNIGHT.plusHours(18),
        )

        // Act
        val result = inputTimeGap.isOverlappingWithAny(timeGaps)

        // Assert
        Assertions.assertThat(result).isTrue()
    }
}