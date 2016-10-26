package lt.markmerkk.utils.graphs

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNotNull

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-10-26
 */
class GraphDrawerXYBarsAssembleIssuesTest {
    val graphDrawer = GraphDrawerXYBars("test_title")

    @Test
    fun emptyInput_emptyMap() {
        // Arrange
        // Act
        val result = graphDrawer.assembleIssues(emptyList())

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun simpleInput_correctMap() {
        // Arrange
        val logs = listOf(
                SimpleLogBuilder(1000)
                        .setTask("TEST-1")
                        .setComment("test_comment")
                        .setStart(1000)
                        .setEnd(2000)
                        .build()
        )

        // Act
        val result = graphDrawer.assembleIssues(logs)

        // Assert
        assertEquals(1, result.size)
        val assertValue = result.get("TEST-1")
        assertNotNull(assertValue)
        assertEquals(assertValue, 1000L)
    }

    @Test
    fun twoDifferentLogs_correctMap() {
        // Arrange
        val logs = listOf(
                SimpleLogBuilder(1000)
                        .setTask("TEST-1")
                        .setComment("test_comment")
                        .setStart(1000)
                        .setEnd(2000)
                        .build(),
                SimpleLogBuilder(1000)
                        .setTask("TEST-2")
                        .setComment("test_comment")
                        .setStart(1000)
                        .setEnd(2000)
                        .build()
        )

        // Act
        val result = graphDrawer.assembleIssues(logs)

        // Assert
        assertEquals(2, result.size)
        val assertValue = result.get("TEST-1")
        assertNotNull(assertValue)
        assertEquals(assertValue, 1000L)
        val assertValue2 = result.get("TEST-2")
        assertNotNull(assertValue2)
        assertEquals(assertValue2, 1000L)
    }

    @Test
    fun twoSame_correctMap() {
        // Arrange
        val logs = listOf(
                SimpleLogBuilder(1000)
                        .setTask("TEST-1")
                        .setComment("test_comment")
                        .setStart(1000)
                        .setEnd(2000)
                        .build(),
                SimpleLogBuilder(1000)
                        .setTask("TEST-1")
                        .setComment("test_comment")
                        .setStart(1000)
                        .setEnd(2000)
                        .build()
        )

        // Act
        val result = graphDrawer.assembleIssues(logs)

        // Assert
        assertEquals(1, result.size)
        val assertValue = result.get("TEST-1")
        assertNotNull(assertValue)
        assertEquals(assertValue, 2000L)
    }

}