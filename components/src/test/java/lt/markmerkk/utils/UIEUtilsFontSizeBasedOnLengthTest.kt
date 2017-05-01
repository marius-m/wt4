package lt.markmerkk.utils

import org.junit.Assert.*
import org.junit.Test

class UIEUtilsFontSizeBasedOnLengthTest {

    @Test
    fun verySmallSize() {
        // Act
        val result = UIEUtils.fontSizeBasedOnLength("1m")

        // Assert
        assertEquals(14.0, result, 0.1)
    }

    @Test
    fun smallSize() {
        // Act
        val result = UIEUtils.fontSizeBasedOnLength("10m")

        // Assert
        assertEquals(12.0, result, 0.1)
    }

    @Test
    fun smallSizePlus() {
        // Act
        val result = UIEUtils.fontSizeBasedOnLength("10m ") // 4 symbols

        // Assert
        assertEquals(10.0, result, 0.1)
    }

    @Test
    fun normalSize() {
        // Act
        val result = UIEUtils.fontSizeBasedOnLength("1h 1m")

        // Assert
        assertEquals(10.0, result, 0.1)
    }

    @Test
    fun normalSizePlus() {
        // Act
        val result = UIEUtils.fontSizeBasedOnLength("1h 10m")

        // Assert
        assertEquals(8.0, result, 0.1)
    }

    @Test
    fun bigSize() {
        // Act
        val result = UIEUtils.fontSizeBasedOnLength("1d 1h 10m")

        // Assert
        assertEquals(8.0, result, 0.1)
    }
}