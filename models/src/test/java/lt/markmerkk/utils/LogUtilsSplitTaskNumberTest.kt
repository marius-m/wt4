package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

class LogUtilsSplitTaskNumberTest {

    @Test
    fun testEmpty() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber(""))
    }

    @Test
    fun noDash() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber("TT12"))
    }

    @Test
    fun valid() {
        assertEquals(12, LogUtils.splitTaskNumber("TT-12"))
    }

    @Test
    fun numInCode() {
        assertEquals(12, LogUtils.splitTaskNumber("TT2EE-12"))
    }

    @Test
    fun lowerCase() {
        assertEquals(12, LogUtils.splitTaskNumber("tt-12"))
    }

    @Test
    fun lowerCase_numInCode() {
        assertEquals(12, LogUtils.splitTaskNumber("tt2ee-12"))
    }

    @Test
    fun lowerCase_noDash() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber("tt212"))
    }

    @Test
    fun justNumber() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber("212"))
    }

    @Test
    fun malformed() {
        assertEquals(LogUtils.NO_NUMBER, LogUtils.splitTaskNumber("-212"))
    }
}