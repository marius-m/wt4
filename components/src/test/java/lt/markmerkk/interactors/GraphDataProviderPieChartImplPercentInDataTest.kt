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
class GraphDataProviderPieChartImplPercentInDataTest {

    val provider = GraphDataProviderPieChartImpl()

    @Test
    fun empty_returnZero() {
        // Arrange
        val logs = emptyList<SimpleLog>()

        // Act
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun validOne_returnOne() {
        // Arrange
        val log = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-4")
                .setComment("test_comment")
                .build()
        val logs = listOf(log)

        // Act
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun twoEqual_returnSplit() {
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
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(50.0, result, 0.001)
    }

    @Test
    fun threeEqual_returnSplit() {
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
        val log3 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-6")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2, log3)

        // Act
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(33.3, result, 0.1)
    }

    @Test
    fun threeEqual_twoSameLogs_returnSplit() {
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
                .setTask("WT-6")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2, log3)

        // Act
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(66.6, result, 0.1)
    }

    @Test
    fun threeEqual_twoSameLogs_oneInvalid_ignoreInvalid() {
        // Arrange
        val log1 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("111") // Invalid task
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
                .setTask("WT-6")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2, log3)

        // Act
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(50.0, result, 0.1)
    }

    @Test
    fun filterAll_returnAll() {
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
        val log3 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("WT-6")
                .setComment("test_comment")
                .build()
        val logs = listOf(log1, log2, log3)

        // Act
        val result = provider.percentInData("WT", logs)

        // Assert
        assertEquals(100.0, result, 0.1)
    }
}