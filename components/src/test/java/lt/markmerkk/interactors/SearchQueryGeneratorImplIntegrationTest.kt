package lt.markmerkk.interactors

import lt.markmerkk.LikeQueryGeneratorImpl
import lt.markmerkk.utils.IssueSplitImpl
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-22
 */
class SearchQueryGeneratorImplIntegrationTest {
    val issueSplit = IssueSplitImpl()
    val keyGenerator = LikeQueryGeneratorImpl("key")
    val descriptionGenerator = LikeQueryGeneratorImpl("description")
    val generator = SearchQueryGeneratorImpl(
            issueSplit = issueSplit,
            keyQueryGenerator = keyGenerator,
            descriptionQueryGenerator = descriptionGenerator
    )

    @Test
    fun emptyInput_returnNoQuery() {
        // Arrange
        // Act
        val result = generator.searchQuery("")

        // Assert
        assertEquals("", result)
    }

    @Test
    fun valid_returnQuery() {
        // Arrange
        // Act
        val result = generator.searchQuery("input1")

        // Assert
        assertEquals("((description like '%%input1%%') OR (key like '%%INPUT-1%%'))", result)
    }

    @Test
    fun valid2_returnQuery() {
        // Arrange
        // Act
        val result = generator.searchQuery("test")

        // Assert
        assertEquals("((description like '%%test%%'))", result)
    }

    @Test
    fun valid3_returnQuery() {
        // Arrange
        // Act
        val result = generator.searchQuery("test test2 test3 test4")

        // Assert
        assertEquals("(" +
                "(" +
                "description like '%%test%%' " +
                "OR description like '%%test2%%' " +
                "OR description like '%%test3%%' " +
                "OR description like '%%test4%%'" +
                ") " +
                "OR " +
                "(key like '%%TEST-2%%')" +
                ")", result)
    }
}