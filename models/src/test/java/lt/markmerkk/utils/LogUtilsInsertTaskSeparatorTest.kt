package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class LogUtilsInsertTaskSeparatorTest {
    @Test
    fun testEmpty() {
        assertEquals("", LogUtils.insertTaskSeperator(""))
    }

    @Test
    fun testNull() {
        assertNull(LogUtils.insertTaskSeperator(null))
    }

    @Test
    fun testValid() {
        assertEquals("tt-11", LogUtils.insertTaskSeperator("tt11"))
    }

    @Test
    fun testInvalid() {
        assertEquals("11", LogUtils.insertTaskSeperator("11"))
    }
}