package lt.markmerkk.utils

import lt.markmerkk.utils.TimeSplit
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/10/16.
 */
class TimeSplitAddStampTest {
    @Test
    fun inputValid_shouldAddComment() {
        // Arrange
        // Act
        val out = TimeSplit.addStamp(1000, 2000, "Simple comment")

        // Assert
        assertThat(out).isEqualTo("03:00 - 03:00 >> Simple comment")
    }

    @Test
    fun inputValidReoccuring_shouldAddComment() {
        // Arrange
        // Act
        var out: String = TimeSplit.addStamp(1000, 2000, "Simple comment")!! // 03:00 - 03:00
        out = TimeSplit.addStamp(1000000, 2000000, out)!! // 03:16 - 03:33
        out = TimeSplit.addStamp(1111111, 6666666, out)!! // 03:18 - 04:51

        // Assert
        assertThat(out).isEqualTo("03:18 - 04:51 >> Simple comment")
    }

    @Test
    fun inputValid2_shouldAddComment() {
        // Arrange
        // Act
        val out = TimeSplit.addStamp(1000, 2000, "a")

        // Assert
        assertThat(out).isEqualTo("03:00 - 03:00 >> a")
    }

    @Test
    fun inputEmptyComment_shouldBeNull() {
        // Arrange
        // Act
        val out = TimeSplit.addStamp(1000, 2000, "")

        // Assert
        assertThat(out).isNull()
    }

    @Test
    fun inputMalformStart_shouldBeValid() {
        // Arrange
        // Act
        val out = TimeSplit.addStamp(-200, 2000, "asdf")

        // Assert
        assertThat(out).isEqualTo("02:59 - 03:00 >> asdf")
    }

    @Test
    fun inputMalformEnd_shouldBeValid() {
        // Arrange
        // Act
        val out = TimeSplit.addStamp(1000, -222, "asdf")

        // Assert
        assertThat(out).isEqualTo("03:00 - 02:59 >> asdf")
    }
}