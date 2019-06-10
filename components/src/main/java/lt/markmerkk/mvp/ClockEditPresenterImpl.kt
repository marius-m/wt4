package lt.markmerkk.mvp

import lt.markmerkk.TimeProvider
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime

class ClockEditPresenterImpl(
        private val view: ClockEditMVP.View,
        private val hourglass: HourGlass,
        private val timeProvider: TimeProvider
) : ClockEditMVP.Presenter {

    override fun onAttach() {
        handleReportDate(
                hourglass,
                timeProvider.roundDateTime(hourglass.startMillis),
                timeProvider.roundDateTime(hourglass.endMillis)
        )
        handleDurationReport(hourglass)
    }

    override fun onDetach() {
    }

    override fun updateDateTime(
            start: DateTime,
            end: DateTime
    ) {
        handleReportDate(
                hourglass,
                start,
                end
        )
        handleDurationReport(hourglass)
    }

    //region Convenience

    /**
     * Handles date reporting back to view
     */
    fun handleReportDate(
            hourglass: HourGlass,
            start: DateTime,
            end: DateTime
    ) {
        if (hourglass.state == HourGlass.State.STOPPED) {
            return
        }
        hourglass.updateTimers(
                LogFormatters.longFormat.print(start),
                LogFormatters.longFormat.print(end)
        )
        if (!hourglass.isValid) {
            return
        }
        view.onDateChange(start, end)
    }

    /**
     * Handles duration report depending on the state of the HG
     */
    fun handleDurationReport(hourglass: HourGlass) {
        if (hourglass.state == HourGlass.State.STOPPED) {
            view.onHintChange(ERROR_CALC_TIME)
            return
        }
        if (!hourglass.isValid) {
            view.onHintChange(ERROR_CALC_TIME)
            return
        }
        val duration = hourglass.endMillis - hourglass.startMillis
        view.onHintChange("Duration: " + LogUtils.formatShortDuration(duration))
    }

    //endregion

    companion object {
        const val ERROR_CALC_TIME = "Error calculating time"
    }

}