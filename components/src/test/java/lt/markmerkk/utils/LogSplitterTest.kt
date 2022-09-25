package lt.markmerkk.utils

import lt.markmerkk.TimeMachine
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.TimeGap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogSplitterTest {

    private val timeProvider = TimeProviderTest()
    private val logSplit = LogSplitter

    @Test
    fun half() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = 50
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(30)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(30)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(50)
    }

    @Test
    fun percent30() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = 30
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(18)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(18)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(30)
    }

    @Test
    fun percent2() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = 2
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(1)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(1)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(2)
    }

    @Test
    fun percent99() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = 99
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(59)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(59)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(99)
    }

    @Test // Impossible
    fun percentZero() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = 0
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(1)
    }

    @Test // Impossible
    fun percentNegative() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = -10
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(1)
    }

    @Test // Impossible
    fun over99() {
        // Assemble
        val start = timeProvider.now()
                .withHourOfDay(10)
                .withMinuteOfHour(0)
        val end = timeProvider.now()
                .withHourOfDay(11)
                .withMinuteOfHour(0)

        // Act
        val resultSplit = logSplit.split(
                timeGap = TimeGap.from(start, end),
                splitPercent = 100
        )

        // Assert
        assertThat(resultSplit.first.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(10)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.first.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.second.start).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.second.end).isEqualTo(
                timeProvider.now()
                        .withHourOfDay(11)
                        .withMinuteOfHour(0)
        )
        assertThat(resultSplit.splitPercent).isEqualTo(99)
    }
}