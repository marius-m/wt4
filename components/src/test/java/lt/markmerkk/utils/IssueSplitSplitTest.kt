package lt.markmerkk.utils

import lt.markmerkk.utils.IssueSplit
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
class IssueSplitSplitTest {
    @Test
    fun simpleValidPhrase_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.split("asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplit.KEY_KEY]).isEqualTo("asdf")
        assertThat(result[IssueSplit.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun simpleValidPhrase2_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.split("tt-12:asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplit.KEY_KEY]).isEqualTo("tt-12")
        assertThat(result[IssueSplit.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun simpleValidPhrase3_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.split("tt12:asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplit.KEY_KEY]).isEqualTo("tt12")
        assertThat(result[IssueSplit.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun simpleValidPhrase4_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.split(":asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplit.KEY_KEY]).isEqualTo(":asdf")
        assertThat(result[IssueSplit.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun emptyPhrase_shouldReturnEmpty() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.split("")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplit.KEY_KEY]).isEqualTo("")
        assertThat(result[IssueSplit.DESCRIPTION_KEY]).isEqualTo("")
    }
}