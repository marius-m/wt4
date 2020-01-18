package lt.markmerkk.utils

import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

class TimedCommentModifierRemoveStampTest {

    @Test
    fun valid() {
        // Arrange
        // Act
        val out = TimedCommentStamper.removeStamp("15:20 - 16:00 >> comment")

        // Assert
        assertThat(out).isEqualTo("comment")
    }

    @Test
    fun valid2() {
        // Arrange
        // Act
        val out = TimedCommentStamper.removeStamp("19:00 - 21:57 >> Testing time gap application.")

        // Assert
        assertThat(out).isEqualTo("Testing time gap application.")
    }

    @Test
    fun invalidSplit() {
        // Arrange
        // Act
        val out = TimedCommentStamper.removeStamp("15:20 > comment")

        // Assert
        assertThat(out).isEqualTo("15:20 > comment")
    }

    @Test
    fun noSplit() {
        // Arrange
        // Act
        val out = TimedCommentStamper.removeStamp(" comment")

        // Assert
        assertThat(out).isEqualTo("comment")
    }

}