package lt.markmerkk

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-22
 */
class LikeQueryGeneratorImplGenClauseTest {
    val generator = LikeQueryGeneratorImpl("fake_key")

    @Test
    fun empty_noQuery() {
        // Arrange
        // Act
        val result = generator.genClause("")

        // Assert
        assertEquals("", result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyKey_noQuery() {
        // Arrange
        // Act
        val result = LikeQueryGeneratorImpl("") // invalid key

        // Assert
        assertNull(result)
    }

    @Test
    fun one_validQuery() {
        // Arrange
        // Act
        val result = generator.genClause("token1")

        // Assert
        assertEquals("fake_key like '%%token1%%'", result)
    }

}