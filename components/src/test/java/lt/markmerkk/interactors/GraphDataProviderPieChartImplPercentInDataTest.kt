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
    fun empty() {
        // Arrange
        val logs = emptyList<SimpleLog>()

        // Act
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(0.0, result, 0.001)
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
        val result = provider.percentInData("WT-4", logs)

        // Assert
        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun threeEqual() {
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
    fun threeEqual_twoSameLogs() {
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
    fun threeEqual_oneInvalid() {
        // Arrange
        val log1 = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("1111") // Invalid should count as empty one
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
        assertEquals(33.3, result, 0.1)
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