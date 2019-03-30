package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.utils.hourglass.HourGlass
import org.junit.Test
import java.time.LocalDateTime

class ClockEditPresenterImplHandleReportDateTest : AbsClockEditPresenterImplTest() {

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