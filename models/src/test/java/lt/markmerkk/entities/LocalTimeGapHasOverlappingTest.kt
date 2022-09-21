package lt.markmerkk.entities

import org.assertj.core.api.Assertions
import org.joda.time.LocalTime
import org.junit.Assert.*
import org.junit.Test

class LocalTimeGapHasOverlappingTest {

    @Test
    fun noOverlap() {
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
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(8),
                end = LocalTime.MIDNIGHT.plusHours(9),
            )
        )

        // Act
        val result = timeGaps.hasOverlapping()

        // Assert
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun hasOverlap_overlapEnd() {
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
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(8),
                end = LocalTime.MIDNIGHT.plusHours(9).plusMinutes(30),
            )
        )

        // Act
        val result = timeGaps.hasOverlapping()

        // Assert
        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun hasOverlap_overlapStart() {
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
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(15).plusMinutes(30),
                end = LocalTime.MIDNIGHT.plusHours(17),
            )
        )

        // Act
        val result = timeGaps.hasOverlapping()

        // Assert
        Assertions.assertThat(result).isTrue()
    }
}