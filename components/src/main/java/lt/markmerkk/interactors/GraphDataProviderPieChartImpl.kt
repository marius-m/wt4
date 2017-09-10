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
            val taskNameWithoutNumber = LogUtils.splitTaskTitle(log.task) ?: EMPTY_TASK_NAME
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
        val childData = mutableMapOf<String, Double>()
        for (log in logs) {
            var taskName: String = log.task
            if (taskName.isEmpty()) {
                taskName = EMPTY_TASK_NAME
            }
            if (!taskName.contains(filter)) continue
            if (childData.containsKey(taskName)) {
                val accumulatedDuration = log.duration.toDouble() + childData.get(log.task)!!
                childData.put(taskName, accumulatedDuration)
            } else {
                childData.put(taskName, log.duration.toDouble())
            }
        }
        return childData
    }

    override fun percentInData(taskName: String, logs: List<SimpleLog>): Double {
        if (logs.isEmpty()) return 0.0
        val total = logs
                .sumByDouble { it.duration.toDouble() }
        val used = timeSpentInData(taskName, logs)
        return used * 100 / total
    }

    override fun timeSpentInData(taskName: String, logs: List<SimpleLog>): Int {
        if (taskName.isEmpty() || EMPTY_TASK_NAME == taskName) {
            return logs.filter { it.task.isEmpty() || it.task == EMPTY_TASK_NAME }
                    .sumBy { it.duration.toInt() }
        }
        return logs
                .filter { it.task.contains(taskName) }
                .sumBy { it.duration.toInt() }
    }

    companion object {
        const val EMPTY_TASK_NAME = "EMPTY_ISSUE"
    }

}