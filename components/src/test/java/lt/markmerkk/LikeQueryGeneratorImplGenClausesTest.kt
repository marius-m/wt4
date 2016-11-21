package lt.markmerkk

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-22
 */
class LikeQueryGeneratorImplGenClausesTest {
    val generator = LikeQueryGeneratorImpl("fake_key")

    @Test
    fun empty_noQuery() {
        // Arrange
        // Act
        val result = generator.genClauses("")

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun one_validQuery() {
        // Arrange
        // Act
        val result = generator.genClauses("token1")

        // Assert
        assertEquals(1, result.size)
        assertEquals("fake_key like '%%%token1%%'", result.get(0))
    }

    @Test
    fun two_validQuery() {
        // Arrange
        // Act
        val result = generator.genClauses("token1 token2")

        // Assert
        assertEquals(2, result.size)
        assertEquals("fake_key like '%%%token1%%'", result.get(0))
        assertEquals("fake_key like '%%%token2%%'", result.get(1))
    }

}