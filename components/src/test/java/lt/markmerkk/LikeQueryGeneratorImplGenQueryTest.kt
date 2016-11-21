package lt.markmerkk

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-22
 */
class LikeQueryGeneratorImplGenQueryTest {
    val generator = LikeQueryGeneratorImpl("fake_key")

    @Test
    fun empty_noQuery() {
        // Arrange
        // Act
        val result = generator.genQuery(emptyList())

        // Assert
        assertEquals("", result)
    }

    @Test
    fun one_validQuery() {
        // Arrange
        // Act
        val result = generator.genQuery(listOf(
                "valid_query1"
        ))

        // Assert
        assertEquals("(valid_query1)", result)
    }

    @Test
    fun two_validQuery() {
        // Arrange
        // Act
        val result = generator.genQuery(listOf(
                "valid_query1",
                "valid_query2"
        ))

        // Assert
        assertEquals("(valid_query1 OR valid_query2)", result)
    }

    @Test
    fun three_validQuery() {
        // Arrange
        // Act
        val result = generator.genQuery(listOf(
                "valid_query1",
                "valid_query2",
                "valid_query3"
        ))

        // Assert
        assertEquals("(valid_query1 OR valid_query2 OR valid_query3)", result)
    }

    @Test
    fun three_oneEmpty_skipEmpty() {
        // Arrange
        // Act
        val result = generator.genQuery(listOf(
                "valid_query1",
                "",
                "valid_query3"
        ))

        // Assert
        assertEquals("(valid_query1 OR valid_query3)", result)
    }

}