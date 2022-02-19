package lt.markmerkk.timeselect

import org.joda.time.LocalTime

interface TimePickerContract {
    interface View {
        fun renderHeader(localTime: LocalTime)
        fun renderSelection(localTime: LocalTime)
    }
    interface Presenter {
        val timeSelection: LocalTime

        fun onAttach()
        fun onDetach()
        fun selectTime(time: LocalTime)
        fun selectHour(hour: Int)
        fun selectMinute(minute: Int)
        fun plusMinute(minuteStep: Int)
        fun minusMinute(minuteStep: Int)
        fun plusHour(hourStep: Int)
        fun minusHour(hourStep: Int)
    }
}