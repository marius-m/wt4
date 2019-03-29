package lt.markmerkk.interactors

import com.nhaarman.mockitokotlin2.mock
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.mvp.HostServicesInteractor
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-22
 */
class GraphDataProviderXYImplAssembleTest {
    val dataProvider = GraphDataProviderXYImpl()

    @Test
    fun emptyInput_emptyMap() {
        // Arrange
        // Act
        val result = dataProvider.assembleIssues(emptyList())

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
        val result = dataProvider.assembleIssues(logs)

        // Assert
        assertEquals(1, result.size)
        val assertValue = result.get("TEST-1")
        kotlin.test.assertNotNull(assertValue)
        assertEquals(1000L, assertValue)
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
        val result = dataProvider.assembleIssues(logs)

        // Assert
        assertEquals(2, result.size)
        val assertValue = result.get("TEST-1")
        kotlin.test.assertNotNull(assertValue)
        assertEquals(1000L, assertValue)
        val assertValue2 = result.get("TEST-2")
        kotlin.test.assertNotNull(assertValue2)
        assertEquals(1000L, assertValue2)
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
        val result = dataProvider.assembleIssues(logs)

        // Assert
        assertEquals(1, result.size)
        val assertValue = result.get("TEST-1")
        kotlin.test.assertNotNull(assertValue)
        assertEquals(2000L, assertValue)
    }
}