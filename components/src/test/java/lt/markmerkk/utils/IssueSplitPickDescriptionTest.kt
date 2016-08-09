package lt.markmerkk.utils

import lt.markmerkk.utils.IssueSplit
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
class IssueSplitPickDescriptionTest {
    @Test
    fun unsplittablePhrase_shouldNotSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.pickPart("asdf", IssueSplit.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

    @Test
    fun validPhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.pickPart("TT-12 : asdf", IssueSplit.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

    @Test
    fun noSpacesPhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.pickPart("TT-12:asdf", IssueSplit.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

    @Test
    fun unnecessarySpacesPhrase_shouldSplit() {
        // Arrange
        val splitter = IssueSplit()

        // Act
        val result = splitter.pickPart("TT-12a :   asdf    ", IssueSplit.DESCRIPTION_REGEX)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("asdf")
    }

}