package lt.markmerkk.widgets.statistics

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass

class StatisticsPresenter(
    private val hourGlass: HourGlass,
    private val activeDisplayRepository: ActiveDisplayRepository
) : StatisticsContract.Presenter {

    private var view: StatisticsContract.View? = null

    override fun onAttach(view: StatisticsContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun mapData(): Map<String, Long> {
        val ticketDurationMap = activeDisplayRepository.displayLogs
                .map { it.code.code to it.time.duration.millis }
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
            val totalLogged: Long = activeDisplayRepository.totalInMillis()
            val formatTotalLogged = LogUtils.formatShortDurationMillis(totalLogged)
            val totalRunning = hourGlass.duration.millis
            val formatTotalRunning = LogUtils.formatShortDurationMillis(totalRunning)
            val formatTotal = LogUtils.formatShortDurationMillis(totalLogged + totalRunning)
            return "Logged ($formatTotalLogged) + Running ($formatTotalRunning) = $formatTotal"
        } else {
            val totalLogged = activeDisplayRepository.totalInMillis()
            val formatTotalLogged = LogUtils.formatShortDurationMillis(totalLogged)
            return "Logged ($formatTotalLogged)"
        }
    }

}