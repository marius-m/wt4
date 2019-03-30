package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.utils.hourglass.HourGlass
import org.junit.Test

class ClockEditPresenterImplHandleDurationReportTest : AbsClockEditPresenterImplTest() {

    @Test
    fun handleValid_reportDuration() {
        // Assemble
        // Act
        presenter.handleDurationReport(hourglass)

        // Assert
        verify(view).onHintChange(any())
    }

    @Test
    fun handleTimeNotValid_reportError() {
        // Assemble
        doReturn(false).whenever(hourglass).isValid

        // Act
        presenter.handleDurationReport(hourglass)

        // Assert
        verify(view).onHintChange("Error calculating time")
    }

    @Test
    fun handleNotRunning_reportError() {
        // Assemble
        doReturn(HourGlass.State.STOPPED).whenever(hourglass).state
        doReturn(true).whenever(hourglass).isValid

        // Act
        presenter.handleDurationReport(hourglass)

        // Assert
        verify(view).onHintChange("Error calculating time")
    }

}