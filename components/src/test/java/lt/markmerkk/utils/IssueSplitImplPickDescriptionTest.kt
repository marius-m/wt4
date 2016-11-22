package lt.markmerkk.utils

import lt.markmerkk.utils.IssueSplitImpl
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
class IssueSplitImplPickDescriptionTest {
    @Test
    fun unsplittablePhrase_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("asdf", IssueSplitImpl.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

    @Test
    fun validPhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("TT-12 : asdf", IssueSplitImpl.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

    @Test
    fun noSpacesPhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("TT-12:asdf", IssueSplitImpl.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

    @Test
    fun unnecessarySpacesPhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplitImpl()

        // Act
        val result = splitter.pickPart("TT-12a :   asdf    ", IssueSplitImpl.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

}