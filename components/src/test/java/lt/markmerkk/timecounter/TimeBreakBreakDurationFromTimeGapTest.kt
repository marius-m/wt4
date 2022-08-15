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
        val timeWork = LocalTimeGap.from(
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
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun doesNotIncludeBreak_timeGapWayBefore() {
        // Assemble
        val timeWork = LocalTimeGap.from(
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
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun doesNotIncludeBreak_timeGapWayAfter() {
        // Assemble
        val timeWork = LocalTimeGap.from(
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
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakInMiddle() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(12),
                end = LocalTime.MIDNIGHT.plusHours(13),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.standardHours(1)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakBiggerThanWork() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(7),
                end = LocalTime.MIDNIGHT.plusHours(19),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.standardHours(9)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakOnEnd() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(16).plusMinutes(30),
                end = LocalTime.MIDNIGHT.plusHours(19),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.standardMinutes(30)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun breakOnStart() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(7),
                end = LocalTime.MIDNIGHT.plusHours(8).plusMinutes(30),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.standardMinutes(30)
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun noBreak_breakOnEdgeStart() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(7),
                end = LocalTime.MIDNIGHT.plusHours(8),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun hasBreak_breakOnEdgeStart() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(8),
                end = LocalTime.MIDNIGHT.plusHours(9).plusMinutes(30),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.standardHours(1)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun noBreak_breakOnEdgeEnd() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(17),
                end = LocalTime.MIDNIGHT.plusHours(19),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.ZERO
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }

    @Test
    fun hasBreak_breakOnEdgeEnd() {
        // Assemble
        val timeWork = LocalTimeGap.from(
            start = LocalTime.MIDNIGHT.plusHours(8),
            end = LocalTime.MIDNIGHT.plusHours(17),
        )
        val timeBreak = TimeBreak(
            timeBreak = LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(15).plusMinutes(30),
                end = LocalTime.MIDNIGHT.plusHours(17),
            )
        )

        // Act
        val result = timeBreak.breakDurationFromTimeGap(timeWork = timeWork)

        // Assert
        val expectDuration = Duration.standardHours(1)
            .plus(Duration.standardMinutes(30))
        Assertions.assertThat(result).isEqualTo(expectDuration)
    }
}