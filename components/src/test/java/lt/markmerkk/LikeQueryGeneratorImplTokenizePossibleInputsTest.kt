package lt.markmerkk

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-21
 */
class LikeQueryGeneratorImplTokenizePossibleInputsTest {

    val generator = LikeQueryGeneratorImpl("fake_key")

    @Test
    fun emptyInput_returnEmpty() {
        // Arrange
        // Act
        val result = generator.tokenizePossibleInputs("")

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun inputOne_returnValid() {
        // Arrange
        // Act
        val result = generator.tokenizePossibleInputs("token1")

        // Assert
        assertEquals(1, result.size)
        assertEquals("token1", result.get(0))
    }

    @Test
    fun inputTwo_returnValid() {
        // Arrange
        // Act
        val result = generator.tokenizePossibleInputs("token1 token2")

        // Assert
        assertEquals(2, result.size)
        assertEquals("token1", result.get(0))
        assertEquals("token2", result.get(1))
    }

    @Test
    fun inputTwo_emptySpaces_returnValid() {
        // Arrange
        // Act
        val result = generator.tokenizePossibleInputs("   token1   token2  ")

        // Assert
        assertEquals(2, result.size)
        assertEquals("token1", result.get(0))
        assertEquals("token2", result.get(1))
    }

    @Test
    fun inputTwo_weirdCharacters_returnValid() {
        // Arrange
        // Act
        val result = generator.tokenizePossibleInputs("token1 ! @ # $ % ^ & * ( ) token2")

        // Assert
        assertEquals(2, result.size)
        assertEquals("token1", result.get(0))
        assertEquals("token2", result.get(1))
    }

    @Test
    fun inputTwo_weirdCharacters2_returnValid() {
        // Arrange
        // Act
        val result = generator.tokenizePossibleInputs("token1 \\:;[],./token2")

        // Assert
        assertEquals(2, result.size)
        assertEquals("token1", result.get(0))
        assertEquals("token2", result.get(1))
    }

}