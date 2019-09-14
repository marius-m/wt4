package lt.markmerkk.entities

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.Duration
import org.junit.Test

class LogTimeFromRawTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun valid() {
        // Assemble
        val now = timeProvider.now()

        // Act
        val result = LogTime.fromRaw(
                timeProvider = timeProvider,
                start = timeProvider.roundMillis(now),
                end = timeProvider.roundMillis(now.plusHours(2))
        )

        // Assert
        assertThat(result.start).isEqualTo(now)
        assertThat(result.end).isEqualTo(now.plusHours(2))
        assertThat(result.duration).isEqualTo(Duration.standardHours(2))
    }

    @Test // should not be possible
    fun startAfterEnd() {
        // Assemble
        val now = timeProvider.now()

        // Act
        val result = LogTime.fromRaw(
                timeProvider = timeProvider,
                start = timeProvider.roundMillis(now.plusHours(2)),
                end = timeProvider.roundMillis(now)
        )

        // Assert
        assertThat(result.start).isEqualTo(now.plusHours(2))
        assertThat(result.end).isEqualTo(now.plusHours(2))
        assertThat(result.duration).isEqualTo(Duration.ZERO)
    }

    @Test
    fun startEqualEnd() {
        // Assemble
        val now = timeProvider.now()

        // Act
        val result = LogTime.fromRaw(
                timeProvider = timeProvider,
                start = timeProvider.roundMillis(now),
                end = timeProvider.roundMillis(now)
        )

        // Assert
        assertThat(result.start).isEqualTo(now)
        assertThat(result.end).isEqualTo(now)
        assertThat(result.duration).isEqualTo(Duration.ZERO)
    }

    @Test // same as start after
    fun endBeforeStart() {
        // Assemble
        val now = timeProvider.now()

        // Act
        val result = LogTime.fromRaw(
                timeProvider = timeProvider,
                start = timeProvider.roundMillis(now),
                end = timeProvider.roundMillis(now.minusHours(2))
        )

        // Assert
        assertThat(result.start).isEqualTo(now)
        assertThat(result.end).isEqualTo(now)
        assertThat(result.duration).isEqualTo(Duration.ZERO)
    }
}