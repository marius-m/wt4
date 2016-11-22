package lt.markmerkk.interactors

import lt.markmerkk.entities.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 */
interface GraphDataProviderXY {
    fun assembleIssues(logs: List<SimpleLog>): Map<String, Number>
}