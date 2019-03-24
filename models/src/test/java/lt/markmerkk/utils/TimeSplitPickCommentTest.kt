package lt.markmerkk.utils

import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/9/16.
 */
class TimeSplitPickCommentTest {
    @Test
    fun inputValid_shouldPick() {
        // Arrange
        // Act
        val out = TimeSplit.removeStamp("15:20 >> comment")

        // Assert
        assertThat(out).isEqualTo("comment")
    }

    @Test
    fun inputValidInvalidSplit_shouldPick() {
        // Arrange
        // Act
        val out = TimeSplit.removeStamp("15:20 > comment")

        // Assert
        assertThat(out).isEqualTo("15:20 > comment")
    }

    @Test
    fun inputValidNoSplit_shouldPick() {
        // Arrange
        // Act
        val out = TimeSplit.removeStamp(" comment")

        // Assert
        assertThat(out).isEqualTo("comment")
    }

}