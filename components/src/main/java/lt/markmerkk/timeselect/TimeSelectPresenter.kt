package lt.markmerkk.timeselect

import lt.markmerkk.ViewProvider
import org.joda.time.LocalTime

class TimeSelectPresenter(
    private val view: ViewProvider<TimeSelectContract.View>
): TimeSelectContract.Presenter {

    private var selection: LocalTime = LocalTime(0, 0)

    override val timeSelection: LocalTime
        get() = selection

    override fun onAttach() {
    }

    override fun onDetach() {
    }

    override fun selectTime(hour: Int, minute: Int) {
        this.selection = LocalTime(hour, minute)
        view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
    }

    override fun selectTime(time: LocalTime) {
        this.selection = time
        view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
    }

    override fun selectHour(hour: Int, render: Boolean) {
        this.selection = this.selection.withHourOfDay(hour)
        if (render) {
            view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
        }
    }

    override fun selectMinute(minute: Int, render: Boolean) {
        this.selection = this.selection.withMinuteOfHour(minute)
        if (render) {
            view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
        }
    }

    override fun plusMinute(minuteStep: Int) {
        this.selection = this.selection.plusMinutes(minuteStep)
        view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
    }

    override fun minusMinute(minuteStep: Int) {
        this.selection = this.selection.minusMinutes(minuteStep)
        view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
    }

    override fun plusHour(hourStep: Int) {
        this.selection = this.selection.plusHours(hourStep)
        view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
    }

    override fun minusHour(hourStep: Int) {
        this.selection = this.selection.minusHours(hourStep)
        view.get()?.renderSelection(this.selection.hourOfDay, this.selection.minuteOfHour)
    }
}