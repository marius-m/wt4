package lt.markmerkk.utils.graphs

import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-20
 */
class GraphDrawerPieDrilldownAssembleParentDataTest {
    val drawer = GraphDrawerPieDrilldown("test_drawer")

    @Test
    fun empty_emptyMap() {
        // Arrange
        // Act
        val result = drawer.assembleParentData(emptyList())

        // Assert
        assertEquals(0, result.size)
    }

}