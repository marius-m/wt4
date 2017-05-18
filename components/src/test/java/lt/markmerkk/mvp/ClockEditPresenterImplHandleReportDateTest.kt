package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.utils.hourglass.HourGlass
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-05-15
 */
class ClockEditPresenterImplHandleReportDateTest {
    @Mock lateinit var hourglass: HourGlass
    @Mock lateinit var view: ClockEditMVP.View
    lateinit var presenter: ClockEditPresenterImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = ClockEditPresenterImpl(view, hourglass)
    }

    @Test
    fun handleValid_reportDate() {
        // Assemble
        doReturn(true).whenever(hourglass).isValid

        // Act
        presenter.handleReportDate(hourglass, LocalDateTime.now(), LocalDateTime.now())

        // Assert
        verify(view).onDateChange(any(), any())
        verify(hourglass).updateTimers(any(), any())
    }

    @Test
    fun invalidDate_ignore() {
        // Assemble
        doReturn(false).whenever(hourglass).isValid

        // Act
        presenter.handleReportDate(hourglass, LocalDateTime.now(), LocalDateTime.now())

        // Assert
        verify(view, never()).onDateChange(any(), any())
        verify(hourglass).updateTimers(any(), any())
    }

    @Test
    fun notRunning_ignore() {
        // Assemble
        doReturn(false).whenever(hourglass).isValid
        doReturn(HourGlass.State.STOPPED).whenever(hourglass).state

        // Act
        presenter.handleReportDate(hourglass, LocalDateTime.now(), LocalDateTime.now())

        // Assert
        verify(view, never()).onDateChange(any(), any())
        verify(hourglass, never()).updateTimers(any(), any())
    }
}