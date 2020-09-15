package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXDatePicker
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalTime

class UIBridgeDateTimeHandler2GenerateTimeTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun valid() {
        // Assemble
        val timeFrom = LocalTime.of(0, 0)
        val timeTo = LocalTime.of(2, 30)

        // Act
        val result = UIBridgeDateTimeHandler2.generateTime(timeFrom, timeTo)

        // Assert
        assertThat(result).containsExactly(
                LocalTime.of(0, 0),
                LocalTime.of(0, 30),
                LocalTime.of(1, 0),
                LocalTime.of(1, 30),
                LocalTime.of(2, 0),
                LocalTime.of(2, 30)
        )
    }

    @Test
    fun unEven() {
        // Assemble
        val timeFrom = LocalTime.of(0, 0)
        val timeTo = LocalTime.of(2, 24)

        // Act
        val result = UIBridgeDateTimeHandler2.generateTime(timeFrom, timeTo)

        // Assert
        assertThat(result).containsExactly(
                LocalTime.of(0, 0),
                LocalTime.of(0, 30),
                LocalTime.of(1, 0),
                LocalTime.of(1, 30),
                LocalTime.of(2, 0),
                LocalTime.of(2, 24)
        )
    }

    @Test
    fun midnight_rollOver() {
        // Assemble
        val timeFrom = LocalTime.of(0, 0)
        val timeTo = LocalTime.of(23, 59)

        // Act
        val result = UIBridgeDateTimeHandler2.generateTime(timeFrom, timeTo)

        // Assert
        assertThat(result).isNotEmpty()
    }

    @Test
    fun zeroes() {
        // Assemble
        val timeFrom = LocalTime.of(0, 0)
        val timeTo = LocalTime.of(0, 0)

        // Act
        val result = UIBridgeDateTimeHandler2.generateTime(timeFrom, timeTo)

        // Assert
        assertThat(result).containsExactly(
                LocalTime.of(0, 0)
        )
    }

    @Test
    fun noGap() {
        // Assemble
        val timeFrom = LocalTime.of(0, 0)
        val timeTo = LocalTime.of(0, 0)

        // Act
        val result = UIBridgeDateTimeHandler2.generateTime(timeFrom, timeTo)

        // Assert
        assertThat(result).containsExactly(
                LocalTime.of(0, 0)
        )
    }

    @Test
    fun fromMoreThanTo() {
        // Assemble
        val timeFrom = LocalTime.of(1, 30)
        val timeTo = LocalTime.of(0, 30)

        // Act
        val result = UIBridgeDateTimeHandler2.generateTime(timeFrom, timeTo)

        // Assert
        assertThat(result).isEmpty()
    }
}