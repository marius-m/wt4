package lt.markmerkk.utils

import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
class IssueSplitSplitImplTest {
    @Test
    fun simpleValidPhrase_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.split("asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplitImpl.KEY_KEY]).isEqualTo("asdf")
        assertThat(result[IssueSplitImpl.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun simpleValidPhrase2_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.split("tt-12:asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplitImpl.KEY_KEY]).isEqualTo("tt-12")
        assertThat(result[IssueSplitImpl.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun simpleValidPhrase3_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.split("tt12:asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplitImpl.KEY_KEY]).isEqualTo("tt12")
        assertThat(result[IssueSplitImpl.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun simpleValidPhrase4_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.split(":asdf")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplitImpl.KEY_KEY]).isEqualTo(":asdf")
        assertThat(result[IssueSplitImpl.DESCRIPTION_KEY]).isEqualTo("asdf")
    }

    @Test
    fun emptyPhrase_shouldReturnEmpty() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.split("")

        // Assert
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result[IssueSplitImpl.KEY_KEY]).isEqualTo("")
        assertThat(result[IssueSplitImpl.DESCRIPTION_KEY]).isEqualTo("")
    }
}