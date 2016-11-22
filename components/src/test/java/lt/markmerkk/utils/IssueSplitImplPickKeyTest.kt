package lt.markmerkk.utils

import lt.markmerkk.utils.IssueSplitImpl
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
class IssueSplitImplPickKeyTest {
    @Test
    fun unsplittablePhrase_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("tt-123", IssueSplitImpl.KEY_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("tt-123")
    }

    @Test
    fun splittablePhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("tt-12:asdf", IssueSplitImpl.KEY_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("tt-12")
    }

    @Test
    fun splittablePhrase2_shouldSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart(":asdf", IssueSplitImpl.KEY_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(":asdf")
    }

    @Test
    fun unsplittablePhrase2_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("tt-12asdf", IssueSplitImpl.KEY_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("tt-12asdf")
    }

}