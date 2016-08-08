package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class LogUtilsValidateTaskTitleTest {
    @Test
    fun testEmpty() {
        assertEquals("", LogUtils.validateTaskTitle(""))
    }

    @Test
    fun testValid1() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("TT-11"))
    }

    @Test
    fun testValid2() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("TT11"))
    }

    @Test
    fun testValidLowerCase1() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("tt11"))
    }

    @Test
    fun testValidLowerCase2() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("tt-11"))
    }

    @Test
    fun testInvalidSpaces() {
        assertEquals("", LogUtils.validateTaskTitle("TT - 11"))
    }

    @Test
    fun testValidMalformed1() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("at TT-11 asdc"))
    }

    @Test
    fun testValidMalformed2() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("at TT11 asdc"))
    }

    @Test
    fun testValidMalformed3() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("at tt11 asdc"))
    }

    @Test
    fun testValidLinebreak1() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("\ntt11\n"))
    }

    @Test
    fun testValidLinebreak2() {
        assertEquals("TT-11", LogUtils.validateTaskTitle("\n tt11"))
    }
}