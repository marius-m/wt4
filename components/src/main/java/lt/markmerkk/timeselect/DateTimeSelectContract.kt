package lt.markmerkk.timeselect

interface DateTimeSelectContract {
    interface View {
        fun renderSelection(hour: Int, minute: Int)
    }
    interface Presenter {
        fun onAttach()
        fun onDetach()
        fun selectTime(hour: Int, minute: Int)
        fun plusMinute(minuteStep: Int)
        fun minusMinute(minuteStep: Int)
        fun plusHour(hourStep: Int)
        fun minusHour(hourStep: Int)
    }
}