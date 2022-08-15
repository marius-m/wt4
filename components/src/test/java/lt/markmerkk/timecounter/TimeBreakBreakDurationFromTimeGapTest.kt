package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Test

class TimeBreakBreakDurationFromTimeGapTest {

    @Test
    fun doesNotIncludeBreak1() {
        // Assemble
        val timeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(9),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = timeGap)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun doesNotIncludeBreak_timeGapWayBefore() {
        // Assemble
        val timeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(11).plusMinutes(30),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(12),
                end = LocalTime.MIDNIGHT.plusHours(13),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = timeGap)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun doesNotIncludeBreak_timeGapWayAfter() {
        // Assemble
        val timeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(15),
            end = LocalTime.MIDNIGHT.plusHours(18),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(12),
                end = LocalTime.MIDNIGHT.plusHours(13),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = timeGap)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakInMiddle() {
        // Assemble
        val timeGap = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(15),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = timeGap)

        // Assert
        val expectDuration = Duration.standardHours(2)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakOnEnd() {
        // Assemble
        val inputTime = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(9).plusMinutes(30),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = inputTime)

        // Assert
        val expectDuration = Duration.standardMinutes(30)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakOnStart() {
        // Assemble
        val inputTime = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(10).plusMinutes(30),
            end = LocalTime.MIDNIGHT.plusHours(15),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = inputTime)

        // Assert
        val expectDuration = Duration.standardMinutes(30)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakOnEdgeStart() {
        // Assemble
        val inputTime = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(9),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = inputTime)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakOnEdgeEnd() {
        // Assemble
        val inputTime = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(11),
            end = LocalTime.MIDNIGHT.plusHours(13),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeGap = inputTime)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }
}