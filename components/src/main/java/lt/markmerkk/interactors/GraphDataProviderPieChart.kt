package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2016-11-20
 */
interface GraphDataProviderPieChart {
    /**
     * Gather data to dissect all issues.
     * For ex.: WT, TEST, RND (parent nodes only)
     */
    fun assembleParentData(logs: List<SimpleLog>): Map<String, Double>

    /**
     * Gather data on a child and its issues.
     * Fox ex.: WT-11, WT-2, WT-4
     * @param filter child node to filter
     */
    fun assembleChildData(logs: List<SimpleLog>, filter: String): Map<String, Double>

    /**
     * Traverses through data to check how much % of the total is used on issue/issues through task name
     */
    fun percentInData(taskName: String, logs: List<SimpleLog>): Double

    /**
     * Traverses through data to check how much time spent on issue/issues through task name
     */
    fun timeSpendInData(taskName: String, logs: List<SimpleLog>): Double

}