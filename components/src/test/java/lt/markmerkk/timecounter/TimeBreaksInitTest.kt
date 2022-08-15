package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.Assert.*
import org.junit.Test

class TimeBreaksInitTest {
    @Test
    fun empty() {
        // Assemble
        // Act
        val result = TimeBreaks.fromTimeGaps()

        // Assert
        Assertions.assertThat(result.breaks).isEmpty()
    }

    @Test
    fun valid_oneTime() {
        // Assemble
        // Act
        val result = TimeBreaks.fromTimeGaps(
            timeGaps = arrayOf(
                LocalTimeGap.from(
                    start = LocalTime.MIDNIGHT.plusHours(9),
                    end = LocalTime.MIDNIGHT.plusHours(11),
                )
            )
        )

        // Assert
        Assertions.assertThat(result.breaks).isNotEmpty()
        val expectDuration = Duration.standardHours(2)
        Assertions.assertThat(result.duration()).isEqualTo(expectDuration)
    }

    @Test
    fun valid_twoTimes() {
        // Assemble
        // Act
        val result = TimeBreaks.fromTimeGaps(
            timeGaps = arrayOf(
                LocalTimeGap.from(
                    start = LocalTime.MIDNIGHT.plusHours(9),
                    end = LocalTime.MIDNIGHT.plusHours(11),
                ),
                LocalTimeGap.from(
                    start = LocalTime.MIDNIGHT.plusHours(12),
                    end = LocalTime.MIDNIGHT.plusHours(14),
                ),
            )
        )

        // Assert
        Assertions.assertThat(result.breaks).containsExactly(
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(11),
            ),
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(12),
                end = LocalTime.MIDNIGHT.plusHours(14),
            ),
        )
        val expectDuration = Duration.standardHours(4)
        Assertions.assertThat(result.duration()).isEqualTo(expectDuration)
    }

    @Test
    fun valid_twoTimes_overlapping() {
        // Assemble
        // Act
        val result = TimeBreaks.fromTimeGaps(
            timeGaps = arrayOf(
                LocalTimeGap.from(
                    start = LocalTime.MIDNIGHT.plusHours(9),
                    end = LocalTime.MIDNIGHT.plusHours(12),
                ),
                LocalTimeGap.from(
                    start = LocalTime.MIDNIGHT.plusHours(11),
                    end = LocalTime.MIDNIGHT.plusHours(14),
                ),
            )
        )

        // Assert
        Assertions.assertThat(result.breaks).containsExactly(
            LocalTimeGap.from(
                start = LocalTime.MIDNIGHT.plusHours(9),
                end = LocalTime.MIDNIGHT.plusHours(12),
            ),
        )
        val expectDuration = Duration.standardHours(3)
        Assertions.assertThat(result.duration()).isEqualTo(expectDuration)
    }
}