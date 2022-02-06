package lt.markmerkk.timeselect

import org.joda.time.LocalTime

interface TimeSelectContract {
    interface View {
        fun renderSelection(hour: Int, minute: Int)
    }
    interface Presenter {
        val timeSelection: LocalTime

        fun onAttach()
        fun onDetach()
        fun selectTime(hour: Int, minute: Int)
        fun selectTime(time: LocalTime)
        fun selectHour(hour: Int, render: Boolean)
        fun selectMinute(minute: Int, render: Boolean)
        fun plusMinute(minuteStep: Int)
        fun minusMinute(minuteStep: Int)
        fun plusHour(hourStep: Int)
        fun minusHour(hourStep: Int)
    }
}