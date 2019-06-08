package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.TimeMachine
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.utils.hourglass.HourGlass
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalTime

class ClockEditPresenterImplTest {

    @Mock lateinit var hourglass: HourGlass
    @Mock lateinit var view: ClockEditMVP.View
    lateinit var presenter: ClockEditPresenterImpl

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = ClockEditPresenterImpl(view, hourglass, timeProvider)
    }

    @Test
    fun onAttach_dateChange() {
        // Assemble
        doReturn(HourGlass.State.RUNNING).whenever(hourglass).state
        doReturn(true).whenever(hourglass).isValid

        // Act
        presenter.onAttach()

        // Assert
        verify(view).onDateChange(any(), any())
        verify(view).onHintChange(any())
    }

    @Test // in theory should not be possible
    fun onAttach_hourglassNotRunning_ignore() {
        // Assemble
        doReturn(HourGlass.State.STOPPED).whenever(hourglass).state

        // Act
        presenter.onAttach()

        // Assert
        verify(view, never()).onDateChange(any(), any())
        verify(view).onHintChange("Error calculating time")
    }

    @Test
    fun onUpdateDateTime_valid_trigger() {
        // Assemble
        // Act
        presenter.updateDateTime(
                TimeMachine.now(),
                TimeMachine.now()
        )

        // Assert
        verify(view).onHintChange(any())
    }

    @Test
    fun onUpdateDateTime_invaliTimer_trigger() {
        // Assemble
        doReturn(false).whenever(hourglass).isValid

        // Act
        presenter.updateDateTime(
                TimeMachine.now(),
                TimeMachine.now()
        )

        // Assert
        verify(view).onHintChange("Error calculating time")
    }

}