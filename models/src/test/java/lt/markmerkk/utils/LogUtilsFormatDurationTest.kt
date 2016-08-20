package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class LogUtilsFormatDurationTest {

    @Test
    fun testEmpty() {
        assertEquals("0s", LogUtils.formatDuration(0))
    }

    @Test
    fun testLowSecond() {
        assertEquals("0s", LogUtils.formatDuration(60))
    }

    @Test
    fun testSeconds() {
        val durationMillis: Long = 1000
        assertEquals("1s", LogUtils.formatDuration(durationMillis))
    }

    @Test
    fun testMinutes() {
        val durationMillis = (60 * 1000).toLong()
        assertEquals("1m", LogUtils.formatDuration(durationMillis))
    }

    @Test
    fun testMinutesAndSeconds() {
        val durationMillis = (60 * 1000 + 2000).toLong()
        assertEquals("1m 2s", LogUtils.formatDuration(durationMillis))
    }

    @Test
    fun testHours() {
        val durationMillis = (60 * 60 * 1000 + 10 * 60 * 1000 + 3000).toLong()
        assertEquals("1h 10m 3s", LogUtils.formatDuration(durationMillis))
    }

    @Test
    fun testDays() {
        val durationMillis = (2 * 24 * 60 * 60 * 1000 + 2 * 60 * 60 * 1000 + 20 * 60 * 1000 + 3000).toLong()
        assertEquals("50h 20m 3s", LogUtils.formatDuration(durationMillis))
    }
}