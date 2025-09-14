package lt.markmerkk

import org.junit.Assert.*
import org.junit.Test

class WorklogUtilsFormatDurationFromSecondsTest {

    @Test
    @Throws(Exception::class)
    fun testEmpty() {
        assertEquals("0m", WorklogUtils.formatDurationFromSeconds(0))
    }

    @Test
    @Throws(Exception::class)
    fun testNegative() {
        assertEquals("0m", WorklogUtils.formatDurationFromSeconds(-200))
    }

    @Test
    @Throws(Exception::class)
    fun testLowSecond() {
        assertEquals("0m", WorklogUtils.formatDurationFromSeconds(1))
    }

    @Test
    @Throws(Exception::class)
    fun testSeconds() {
        assertEquals("0m", WorklogUtils.formatDurationFromSeconds(59))
    }

    @Test
    @Throws(Exception::class)
    fun testMinutes() {
        assertEquals("1m", WorklogUtils.formatDurationFromSeconds(60))
    }

    @Test
    @Throws(Exception::class)
    fun testMinutesAndSeconds() {
        assertEquals(
            "1m", WorklogUtils.formatDurationFromSeconds(
                (60 // 1 minute
                        + 2).toLong()
            ) // 2 seconds
        )
    }

    @Test
    @Throws(Exception::class)
    fun testMinutesAndSeconds2() {
        assertEquals(
            "2m", WorklogUtils.formatDurationFromSeconds(
                (60 // 1 minute
                        + 72).toLong()
            ) // 72 seconds
        )
    }

    @Test
    @Throws(Exception::class)
    fun testHours() {
        assertEquals(
            "1h 10m", WorklogUtils.formatDurationFromSeconds(
                ((60 * 60) // 1 hour
                        + (10 * 60) // 10 minutes
                        + 3).toLong()
            ) // 3 seconds
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDays() {
        assertEquals(
            "50h 20m", WorklogUtils.formatDurationFromSeconds(
                ((60 * 60 * 50) // 50 hours
                        + (60 * 20) // 20 minutes
                        + (3)).toLong() // s seconds
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDays2() {
        assertEquals(
            "50h 22m", WorklogUtils.formatDurationFromSeconds(
                ((60 * 60 * 50) // 50 hours
                        + (60 * 20) // 20 minutes
                        + (125)).toLong() // 125 seconds
            )
        )
    }

}