package lt.markmerkk.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogUtilsShortFormatDurationTest {

    @Test
    fun zero() {
        assertThat(LogUtils.formatShortDurationMillis(0)).isEqualTo("0m")
    }

    @Test
    fun invalid_negative() {
        assertThat(LogUtils.formatShortDurationMillis(-100)).isEqualTo("0m")
    }

    @Test
    fun lowSecond() {
        assertThat(LogUtils.formatShortDurationMillis(60)).isEqualTo("0m")
    }

    @Test
    fun seconds() {
        val durationMillis: Long = 1000
        assertThat(LogUtils.formatShortDurationMillis(durationMillis)).isEqualTo("0m")
    }

    @Test
    fun minutes() {
        val durationMillis = (60 * 1000).toLong()
        assertThat(LogUtils.formatShortDurationMillis(durationMillis)).isEqualTo("1m")
    }

    @Test
    fun minutesAndSeconds() {
        val durationMillis = (60 * 1000 + 2000).toLong()
        assertThat(LogUtils.formatShortDurationMillis(durationMillis)).isEqualTo("1m")
    }

    @Test
    fun hours() {
        val durationMillis = (60 * 60 * 1000 + 10 * 60 * 1000 + 3000).toLong()
        assertThat(LogUtils.formatShortDurationMillis(durationMillis)).isEqualTo("1h 10m")
    }

    @Test
    fun days() {
        val durationMillis = (2 * 24 * 60 * 60 * 1000 + 2 * 60 * 60 * 1000 + 20 * 60 * 1000 + 3000).toLong()
        assertThat(LogUtils.formatShortDurationMillis(durationMillis)).isEqualTo("50h 20m")
    }
}