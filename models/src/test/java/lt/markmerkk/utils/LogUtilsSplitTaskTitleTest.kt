package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class LogUtilsSplitTaskTitleTest {
    @Test
    fun testEmpty() {
        assertEquals("", LogUtils.splitTaskTitle(""))
    }

    @Test
    fun testValid() {
        assertEquals("TT", LogUtils.splitTaskTitle("TT12"))
    }

    @Test
    fun testValid2() {
        assertEquals("TT", LogUtils.splitTaskTitle("TT-12"))
    }

    @Test
    fun testValid3() {
        assertEquals("TT", LogUtils.splitTaskTitle("tt212"))
    }

    @Test
    fun testInvalid() {
        assertEquals("", LogUtils.splitTaskTitle("212"))
    }
}