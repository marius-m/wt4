package lt.markmerkk.widgets.clock

interface ClockContract {
    interface View {
        fun showActive(timeAsString: String)
        fun showInactive()
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun toggleClock()
        fun cancelClock()
        fun renderClock()
    }
}