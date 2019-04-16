package lt.markmerkk.mvp

import lt.markmerkk.TimeProvider
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClockEditPresenterImpl(
        private val view: ClockEditMVP.View,
        private val hourglass: HourGlass,
        private val timeProvider: TimeProvider
) : ClockEditMVP.Presenter {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")!!

    override fun onAttach() {
        handleReportDate(
                hourglass,
                timeProvider.jLocalDateTimeFrom(hourglass.startMillis),
                timeProvider.jLocalDateTimeFrom(hourglass.endMillis)
        )
        handleDurationReport(hourglass)
    }

    override fun onDetach() {
    }

    override fun updateDateTime(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime
    ) {
        handleReportDate(
                hourglass,
                LocalDateTime.of(startDate, startTime),
                LocalDateTime.of(endDate, endTime)
        )
        handleDurationReport(hourglass)
    }

    //region Convenience

    /**
     * Handles date reporting back to view
     */
    fun handleReportDate(
            hourglass: HourGlass,
            start: LocalDateTime,
            end: LocalDateTime
    ) {
        if (hourglass.state == HourGlass.State.STOPPED) {
            return
        }
        hourglass.updateTimers(
                start.format(dateTimeFormatter),
                end.format(dateTimeFormatter)
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