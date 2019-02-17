package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

class LogUtilsSplitTaskTitleTest {
    @Test
    fun testEmpty() {
        assertEquals("", LogUtils.splitTaskTitle(""))
    }

    @Test
    fun noDash() {
        assertEquals("", LogUtils.splitTaskTitle("TT12"))
    }

    @Test
    fun valid() {
        assertEquals("TT", LogUtils.splitTaskTitle("TT-12"))
    }

    @Test
    fun numInCode() {
        assertEquals("T2EE", LogUtils.splitTaskTitle("T2EE-12"))
    }

    @Test
    fun lowerCase_noDash() {
        assertEquals("", LogUtils.splitTaskTitle("tt212"))
    }

    @Test
    fun lowerCase_valid() {
        assertEquals("TT", LogUtils.splitTaskTitle("tt-212"))
    }

    @Test
    fun lowerCase_numInCode() {
        assertEquals("T2EE", LogUtils.splitTaskTitle("t2ee-212"))
    }

    @Test
    fun testInvalid() {
        assertEquals("", LogUtils.splitTaskTitle("212"))
    }
}