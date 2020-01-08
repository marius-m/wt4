package lt.markmerkk.utils

import lt.markmerkk.TimeProvider
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Test

class LogUtilsShortFormatDurationFromDurationTest {

    @Test
    fun zero() {
        val start = DateTime()
        val end = DateTime()
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("0m")
    }

    @Test
    fun invalid_negative() {
        val start = DateTime()
        val end = DateTime().minusMinutes(10)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("0m")
    }

    @Test
    fun lowSecond() {
        val start = DateTime()
        val end = DateTime().plusMillis(60)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("0m")
    }

    @Test
    fun seconds() {
        val start = DateTime()
        val end = DateTime().plusSeconds(1)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("0m")
    }

    @Test
    fun minutes() {
        val start = DateTime()
        val end = DateTime().plusMinutes(1)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("1m")
    }

    @Test
    fun minutesAndSeconds() {
        val start = DateTime()
        val end = DateTime().plusMinutes(1).plusSeconds(30)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("1m")
    }

    @Test
    fun hours() {
        val start = DateTime()
        val end = DateTime().plusHours(1).plusMinutes(10)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("1h 10m")
    }

    @Test
    fun days() {
        val start = DateTime()
        val end = DateTime().plusHours(50).plusMinutes(20)
        val duration = Duration(start, end)
        assertThat(LogUtils.formatShortDuration(duration)).isEqualTo("50h 20m")
    }
}