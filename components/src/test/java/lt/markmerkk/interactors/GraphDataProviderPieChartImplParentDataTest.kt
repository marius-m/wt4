package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLogBuilder
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-20
 */
class GraphDataProviderPieChartImplParentDataTest {
    val provider = GraphDataProviderPieChartImpl()

    @Test
    fun empty_returnEmpty() {
        // Arrange
        // Act
        val result = provider.assembleParentData(emptyList())

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun one_returnValid() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleParentData(logs)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.containsKey("WT"))
        assertEquals(1000.0, result.get("WT"))
    }

    @Test
    fun twoDifferent_separateTwoLogs() {
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
                .setTask("RND-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2)

        // Act
        val result = provider.assembleParentData(logs)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.containsKey("WT"))
        assertTrue(result.containsKey("RND"))
        assertEquals(1000.0, result.get("WT"))
        assertEquals(1000.0, result.get("RND"))
    }

    @Test
    fun twoSameLogs_returnOneAccumulated() {
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
        val result = provider.assembleParentData(logs)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.containsKey("WT"))
        assertEquals(2000.0, result.get("WT"))
    }

    @Test
    fun invalidTaskName() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("111") // Invalid task name
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleParentData(logs)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.containsKey(GraphDataProviderPieChartImpl.EMPTY_TASK_NAME))
    }

    @Test
    fun emptyTaskName() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("")
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.assembleParentData(logs)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.containsKey(GraphDataProviderPieChartImpl.EMPTY_TASK_NAME))
    }

}