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
            val taskNameWithoutNumber = LogUtils.splitTaskTitle(log.task) ?: continue
            if (parentNodes.containsKey(taskNameWithoutNumber)) {
                val oldValue = parentNodes.get(taskNameWithoutNumber)
                val logValue = log.duration.toDouble() + oldValue!!
                parentNodes.put(
                        taskNameWithoutNumber,
                        logValue
                )
            } else {
                parentNodes.put(
                        taskNameWithoutNumber,
                        log.duration.toDouble()
                )
            }
        }
        return parentNodes
    }

    override fun assembleChildData(logs: List<SimpleLog>, filter: String): Map<String, Double> {
        if (filter.isEmpty()) return emptyMap()
        val childData = mutableMapOf<String, Double>()
        for (log in logs) {
            if (!log.task.contains(filter)) continue
            if (childData.containsKey(log.task)) {
                val accumulatedDuration = log.duration.toDouble() + childData.get(log.task)!!
                childData.put(log.task, accumulatedDuration)
            } else {
                childData.put(log.task, log.duration.toDouble())
            }
        }
        return childData
    }
}