package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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