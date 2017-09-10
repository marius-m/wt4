package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class LogUtilsSplitTaskNumberTest {
    @Test
    fun testEmpty() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber(""))
    }

    @Test
    fun testValid() {
        assertEquals(12, LogUtils.splitTaskNumber("TT12"))
    }

    @Test
    fun testValid2() {
        assertEquals(12, LogUtils.splitTaskNumber("TT-12"))
    }

    @Test
    fun testValid3() {
        assertEquals(212, LogUtils.splitTaskNumber("tt212"))
    }

    @Test
    fun testInvalid() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber("212"))
    }
}