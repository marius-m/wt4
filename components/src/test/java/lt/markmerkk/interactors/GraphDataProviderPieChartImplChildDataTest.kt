package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLogBuilder
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-20
 */
class GraphDataProviderPieChartImplChildDataTest {
    val provider = GraphDataProviderPieChartImpl()

    @Test
    fun empty_returnEmpty() {
        // Arrange
        // Act
        val result = provider.assembleChildData(emptyList(), "WT")

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun validOne() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleChildData(logs, "WT")

        // Assert
        assertEquals(1, result.size)
    }

    @Test // no task name by default is assigned to an empty task name
    fun validOne_noTaskName() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleChildData(logs, GraphDataProviderPieChartImpl.EMPTY_TASK_NAME)

        // Assert
        assertEquals(1, result.size)
    }

    @Test
    fun validTwoSame_returnAccumulated() {
        // Arrange
        val log1 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val log2 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2)

        // Act
        val result = provider.assembleChildData(logs, "WT")

        // Assert
        assertEquals(1, result.size)
        assertEquals(2000.0, result.get("WT-4"))
        assertTrue(result.containsKey("WT-4"))
    }

    @Test
    fun validTwoDifferent_returnSeparate() {
        // Arrange
        val log1 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val log2 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-5")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2)

        // Act
        val result = provider.assembleChildData(logs, "WT")

        // Assert
        assertEquals(2, result.size)
        assertEquals(1000.0, result.get("WT-4"))
        assertTrue(result.containsKey("WT-4"))
        assertEquals(1000.0, result.get("WT-5"))
        assertTrue(result.containsKey("WT-5"))
    }

    @Test
    fun validTwoSame_oneDoesNotBelong() {
        // Arrange
        val log1 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val log2 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val log3 = SimpleLogBuilder() // invalid to filter
                .setStart(1000)
                .setEnd(2000)
                .setTask("RND-1")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2, log3)

        // Act
        val result = provider.assembleChildData(logs, "WT")

        // Assert
        assertEquals(1, result.size)
        assertEquals(2000.0, result.get("WT-4"))
        assertTrue(result.containsKey("WT-4"))
    }

    @Test
    fun invalidFilter() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleChildData(logs, "ASDF") // Invalid filter

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun invalidTaskTitle_searchForEmpty() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("111") // invalid title
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleChildData(logs, GraphDataProviderPieChartImpl.EMPTY_TASK_NAME)

        // Assert
        assertEquals(1, result.size)
    }

}