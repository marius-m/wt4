package lt.markmerkk.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimedCommentStamperRemoveStampTest {

    @Test
    fun inputValid() {
        // Arrange
        // Act
        val out = TimedCommentStamper.removeStamp("15:20 - 16:00 >> comment")

        // Assert
        assertThat(out).isEqualTo("comment")
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
    fun noTimeStamp() {
        // Arrange
        // Act
        val out = TimedCommentStamper.removeStamp(" comment")

        // Assert
        assertThat(out).isEqualTo("comment")
    }

}