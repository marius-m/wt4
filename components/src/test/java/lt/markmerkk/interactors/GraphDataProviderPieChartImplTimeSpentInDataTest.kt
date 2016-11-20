package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-20
 */
class GraphDataProviderPieChartImplTimeSpentInDataTest {

    val provider = GraphDataProviderPieChartImpl()

    @Test
    fun empty_returnZero() {
        // Arrange
        val logs = emptyList<SimpleLog>()

        // Act
        val result = provider.timeSpentInData("WT-4", logs)

        // Assert
        assertEquals(0, result)
    }

    @Test
    fun validOne_returnValid() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.timeSpentInData("WT-4", logs)

        // Assert
        assertEquals(1000, result)
    }

    @Test
    fun validTwoSame_returnValid() {
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
        val result = provider.timeSpentInData("WT-4", logs)

        // Assert
        assertEquals(2000, result)
    }

    @Test
    fun validTwoSame_oneDoesNotBelong_returnValid() {
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
        val log3 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-5") // Task does not include in filter
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2, log3)

        // Act
        val result = provider.timeSpentInData("WT-4", logs)

        // Assert
        assertEquals(2000, result)
    }

    @Test
    fun invalidTaskId_skipInvalidLog() {
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
                .setTask("111") // skip invalid log
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2)

        // Act
        val result = provider.timeSpentInData("WT-4", logs)

        // Assert
        assertEquals(1000, result)
    }

    @Test
    fun emptyFilter_returnZero() {
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
                .setTask("111") // skip invalid log
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2)

        // Act
        val result = provider.timeSpentInData("", logs)

        // Assert
        assertEquals(0, result)
    }

}