package lt.markmerkk.utils

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TimedCommentStamperAddStampTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun allTimeFormatsIncluded() {
        // Arrange
        val start = timeProvider.now()
        val limit = start.plusDays(2)

        // Act
        var end = start
        val comment = "Simple comment"
        while (end.isBefore(limit)) {
            val result = TimedCommentStamper.addStamp(start, end, comment)

            // Assert
            println(result)
            assertThat(result).contains(">>")
            assertThat(result).contains("Simple comment")
            end = end.plusMinutes(1)
        }
    }

    @Test
    fun valid() {
        // Arrange
        val start = timeProvider.now().plusHours(3)
        val end = start.plusMinutes(30)

        // Act
        val result = TimedCommentStamper.addStamp(start, end, "Simple comment")

        // Assert
        assertThat(result).isEqualTo("03:00 - 03:30 >> Simple comment")
    }

    @Test
    fun reoccuring() {
        // Arrange
        val start = timeProvider.now().plusHours(3)
        val end = start.plusMinutes(30)

        // Act
        val result: String = TimedCommentStamper.addStamp(start, end, "Simple comment") // 03:00 - 03:30

        // Arrange
        val start2 = end.plusHours(3)
        val end2 = start2.plusMinutes(30)

        // Act
        val result2 = TimedCommentStamper.addStamp(start2, end2, result) // 06:30 - 07:00

        // Assert
        assertThat(result2).isEqualTo("06:30 - 07:00 >> Simple comment")
    }

    @Test
    fun valid2() {
        // Arrange
        val start = timeProvider.now().plusHours(3)
        val end = start.plusMinutes(30)

        // Act
        val result: String = TimedCommentStamper.addStamp(start, end, "a")

        // Assert
        assertThat(result).isEqualTo("03:00 - 03:30 >> a")
    }

    @Test
    fun emptyComment() {
        // Arrange
        val start = timeProvider.now().plusHours(3)
        val end = start.plusMinutes(30)

        // Act
        val result = TimedCommentStamper.addStamp(start, end, "")

        // Assert
        assertThat(result).isEmpty()
    }

}