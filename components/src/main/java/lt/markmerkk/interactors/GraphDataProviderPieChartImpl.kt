package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.utils.LogUtils

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
class GraphDataProviderPieChartImpl : GraphDataProviderPieChart {
    override fun assembleParentData(logs: List<SimpleLog>): Map<String, Double> {
        val parentNodes = mutableMapOf<String, Double>()
        for(log in logs) {
            val taskNameWithoutNumber = LogUtils.splitTaskTitle(log.task)
            if (taskNameWithoutNumber == null) continue
            parentNodes.put(taskNameWithoutNumber, log.duration.toDouble())
        }
        return parentNodes
    }

    override fun assembleChildData(logs: List<SimpleLog>, filter: String): Map<String, Double> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}