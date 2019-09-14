package lt.markmerkk.widgets.statistics

interface StatisticsContract {
    interface View { }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun mapData(): Map<String, Long>
        fun totalAsString(): String
    }
}