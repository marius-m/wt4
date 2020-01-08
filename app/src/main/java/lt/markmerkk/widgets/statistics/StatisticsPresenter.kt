package lt.markmerkk.widgets.statistics

import lt.markmerkk.LogStorage
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass

class StatisticsPresenter(
        private val logStorage: LogStorage,
        private val hourGlass: HourGlass
): StatisticsContract.Presenter {

    private var view: StatisticsContract.View? = null

    override fun onAttach(view: StatisticsContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun mapData(): Map<String, Long> {
        val ticketDurationMap = logStorage.data
                .map { it.task to it.duration }
        val ticketDurationSumMap = mutableMapOf<String, Long>()
        ticketDurationMap
                .forEach {
                    if (ticketDurationSumMap.contains(it.first)) {
                        val oldDuration = ticketDurationSumMap[it.first]
                        ticketDurationSumMap[it.first] = oldDuration!!.plus(it.second)
                    } else {
                        ticketDurationSumMap[it.first] = it.second
                    }
                }
        if (ticketDurationSumMap.containsKey("")) {
            ticketDurationSumMap.put("Not mapped", ticketDurationSumMap[""]!!)
            ticketDurationSumMap.remove("")
        }
        return ticketDurationSumMap
    }

    override fun totalAsString(): String {
        if (hourGlass.isRunning()) {
            val totalLogged: Long = logStorage.total().toLong()
            val formatTotalLogged = LogUtils.formatShortDurationMillis(totalLogged)
            val totalRunning = hourGlass.duration.millis
            val formatTotalRunning = LogUtils.formatShortDurationMillis(totalRunning)
            val formatTotal = LogUtils.formatShortDurationMillis(totalLogged + totalRunning)
            return "Logged ($formatTotalLogged) + Running ($formatTotalRunning) = $formatTotal"
        } else {
            val totalLogged = logStorage.total().toLong()
            val formatTotalLogged = LogUtils.formatShortDurationMillis(totalLogged)
            return "Logged ($formatTotalLogged)"
        }
    }

}