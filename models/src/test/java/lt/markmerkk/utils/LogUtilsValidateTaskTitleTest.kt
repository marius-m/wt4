package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

class LogUtilsValidateTaskTitleTest {
    @Test
    fun testEmpty() {
        assertEquals("", LogUtils.validateTaskTitle(""))
    }

    @Test
    fun valid() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("TT-11"))
    }

    @Test
    fun numInCode_alid() {
        assertEquals("T2EE-11", LogUtils.validateTaskTitle("T2EE-11"))
    }

    @Test
    fun noDash() {
        assertEquals("", LogUtils.validateTaskTitle("TT11"))
    }

    @Test
    fun lowerCaseNoDash() {
        assertEquals("", LogUtils.validateTaskTitle("tt11"))
    }

    @Test
    fun lowerCase() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("tt-11"))
    }

    @Test
    fun numInCode_lowerCase() {
        assertEquals("T2EE-11", LogUtils.validateTaskTitle("t2ee-11"))
    }

    @Test
    fun spacesInMiddle() {
        assertEquals("", LogUtils.validateTaskTitle("TT - 11"))
    }

    @Test
    fun valid_malformed() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("at TT-11 asdc"))
    }

    @Test
    fun numInCode_malformed() {
        assertEquals("T2EE-11", LogUtils.validateTaskTitle("at T2EE-11 asdc"))
    }

    @Test
    fun malformedNoDash() {
        assertEquals("", LogUtils.validateTaskTitle("at TT11 asdc"))
    }

    @Test
    fun malformedNoDash_lowerCase() {
        assertEquals("", LogUtils.validateTaskTitle("at tt11 asdc"))
    }

    @Test
    fun malformed_lowerCase() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("at tt-11 asdc"))
    }

    @Test
    fun numInCode_malformed_lowerCase() {
        assertEquals("T2EE-11", LogUtils.validateTaskTitle("at t2ee-11 asdc"))
    }

    @Test
    fun noDashWithBreaks() {
        assertEquals("", LogUtils.validateTaskTitle("\ntt11\n"))
    }

    @Test
    fun noDashWithBreaks2() {
        assertEquals("", LogUtils.validateTaskTitle("\n tt11"))
    }
}