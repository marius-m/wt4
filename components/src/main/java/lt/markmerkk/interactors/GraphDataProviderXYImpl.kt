package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 */
class GraphDataProviderXYImpl : GraphDataProviderXY {
    override fun assembleIssues(logs: List<SimpleLog>): Map<String, Number> {
        val mappedLogs = mutableMapOf<String, Number>()
        logs.forEach {
            if (mappedLogs.containsKey(it.task)) {
                mappedLogs.put(it.task, it.duration + mappedLogs.get(it.task) as Long)
            } else {
                mappedLogs.put(it.task, it.duration)
            }
        }
        return mappedLogs
    }
}