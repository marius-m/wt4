package lt.markmerkk.ui_2.bridges

import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalTime

class UIBridgeDateTimeHandler2DefaultTimeRangeTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun valid() {
        // Assemble
        // Act
        val result = UIBridgeDateTimeHandler2.defaultTimeRanges()

        // Assert
        assertThat(result).containsExactly(
                LocalTime.of(0, 0),
                LocalTime.of(0, 30),
                LocalTime.of(1, 0),
                LocalTime.of(1, 30),
                LocalTime.of(2, 0),
                LocalTime.of(2, 30),
                LocalTime.of(3, 0),
                LocalTime.of(3, 30),
                LocalTime.of(4, 0),
                LocalTime.of(4, 30),
                LocalTime.of(5, 0),
                LocalTime.of(5, 30),
                LocalTime.of(6, 0),
                LocalTime.of(6, 30),
                LocalTime.of(7, 0),
                LocalTime.of(7, 30),
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0),
                LocalTime.of(11, 30),
                LocalTime.of(12, 0),
                LocalTime.of(12, 30),
                LocalTime.of(13, 0),
                LocalTime.of(13, 30),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30),
                LocalTime.of(16, 0),
                LocalTime.of(16, 30),
                LocalTime.of(17, 0),
                LocalTime.of(17, 30),
                LocalTime.of(18, 0),
                LocalTime.of(18, 30),
                LocalTime.of(19, 0),
                LocalTime.of(19, 30),
                LocalTime.of(20, 0),
                LocalTime.of(20, 30),
                LocalTime.of(21, 0),
                LocalTime.of(21, 30),
                LocalTime.of(22, 0),
                LocalTime.of(22, 30),
                LocalTime.of(23, 0),
                LocalTime.of(23, 30),
                LocalTime.of(23, 59)
        )
    }

}