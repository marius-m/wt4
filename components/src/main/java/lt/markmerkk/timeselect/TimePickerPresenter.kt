package lt.markmerkk.timeselect

import lt.markmerkk.ViewProvider
import org.joda.time.LocalTime

class TimePickerPresenter(
    private val view: ViewProvider<TimePickerContract.View>
): TimePickerContract.Presenter {

    private var selection: LocalTime = LocalTime(0, 0)

    override val timeSelection: LocalTime
        get() = selection

    override fun onAttach() {
    }

    override fun onDetach() {
    }

    override fun selectTime(time: LocalTime) {
        this.selection = time
        view.invoke { renderHeader(selection) }
        view.invoke { renderSelection(selection) }
    }

    override fun selectHour(hour: Int) {
        this.selection = this.selection.withHourOfDay(hour)
        view.invoke { renderHeader(selection) }
        // Don't change hour selection as this would re-render same UI component
    }

    override fun selectMinute(minute: Int) {
        this.selection = this.selection.withMinuteOfHour(minute)
        view.invoke { renderHeader(selection) }
        // Don't change hour selection as this would re-render same UI component
    }

    override fun plusMinute(minuteStep: Int) {
        this.selection = this.selection.plusMinutes(minuteStep)
        view.invoke { renderHeader(selection) }
        view.invoke { renderSelection(selection) }
    }

    override fun minusMinute(minuteStep: Int) {
        this.selection = this.selection.minusMinutes(minuteStep)
        view.invoke { renderHeader(selection) }
        view.invoke { renderSelection(selection) }
    }

    override fun plusHour(hourStep: Int) {
        this.selection = this.selection.plusHours(hourStep)
        view.invoke { renderHeader(selection) }
        view.invoke { renderSelection(selection) }
    }

    override fun minusHour(hourStep: Int) {
        this.selection = this.selection.minusHours(hourStep)
        view.invoke { renderHeader(selection) }
        view.invoke { renderSelection(selection) }
    }
}