package lt.markmerkk

import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.DeleteJob
import lt.markmerkk.entities.jobs.InsertJob
import lt.markmerkk.entities.jobs.QueryListJob
import lt.markmerkk.entities.jobs.UpdateJob

/**
 * Created by mariusmerkevicius on 1/6/16.
 * Holds all downloaded issues for use
 */
class IssueStorage(
        private var executor: IExecutor
) : IDataStorage<LocalIssue> {

    override val data = emptyList<LocalIssue>() // will not hold issues, as it will fill up cache
    val listeners = mutableListOf<IDataListener<LocalIssue>>()

    var filter: String = ""
        set(value) {
            field = value
            notifyDataChange()
        }

    override fun register(listener: IDataListener<LocalIssue>) {
        listeners.add(listener)
    }

    override fun unregister(listener: IDataListener<LocalIssue>) {
        listeners.remove(listener)
    }

    override fun insert(dataEntity: LocalIssue) {
        executor.execute(InsertJob(LocalIssue::class.java, dataEntity))
        notifyDataChange()
    }

    override fun delete(dataEntity: LocalIssue) {
        executor.execute(DeleteJob(LocalIssue::class.java, dataEntity))
        notifyDataChange()
    }

    override fun update(dataEntity: LocalIssue) {
        executor.execute(UpdateJob(LocalIssue::class.java, dataEntity))
        notifyDataChange()
    }

    override fun notifyDataChange() { // Should not provide data
        listeners.forEach { it.onDataChange(data) }
    }

    override fun customQuery(queryPredicate: String): List<LocalIssue> {
        val query = QueryListJob(LocalIssue::class.java, { queryPredicate })
        executor.execute(query)
        return query.result()
    }

}
