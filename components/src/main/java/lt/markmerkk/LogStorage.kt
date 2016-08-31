package lt.markmerkk

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.DeleteJob
import lt.markmerkk.entities.jobs.InsertJob
import lt.markmerkk.entities.jobs.QueryListJob
import lt.markmerkk.entities.jobs.UpdateJob
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 12/13/15.
 * Represents the storage for simple use.
 */
class LogStorage(
        private var executor: IExecutor
) : IDataStorage<SimpleLog> {

    override var data = mutableListOf<SimpleLog>()

    private var listeners = mutableListOf<IDataListener<SimpleLog>>()
    var displayType = DisplayType.DAY
        set(value) {
            field = value
            notifyDataChange()
        }
    var targetDate = DateTime().withTime(0, 0, 0, 0)
        set(value) {
            field = value.withTime(0, 0, 0, 0)
            notifyDataChange()
        }

    init {
        notifyDataChange()
    }

    fun suggestTargetDate(dateAsString: String) {
        try {
            val newTime = LogFormatters.shortFormatDate.parseDateTime(dateAsString)
            if (targetDate.year == newTime.year
                    && targetDate.monthOfYear == newTime.monthOfYear
                    && targetDate.dayOfMonth == newTime.dayOfMonth) {
                return
            }
            targetDate = newTime
        } catch (e: IllegalArgumentException) {
            logger.error("Error suggesting target date!", e)
        }
    }

    override fun register(listener: IDataListener<SimpleLog>) {
        listeners.add(listener)
    }

    override fun unregister(listener: IDataListener<SimpleLog>) {
        listeners.remove(listener)
    }

    override fun insert(dataEntity: SimpleLog) {
        executor.execute(InsertJob(SimpleLog::class.java, dataEntity))
        notifyDataChange()
    }

    override fun delete(dataEntity: SimpleLog) {
        executor.execute(DeleteJob(SimpleLog::class.java, dataEntity))
        notifyDataChange()
    }

    override fun update(dataEntity: SimpleLog) {
        executor.execute(UpdateJob(SimpleLog::class.java, dataEntity))
        notifyDataChange()
    }

    override fun notifyDataChange() {
        val queryJob: QueryListJob<SimpleLog>
        when (displayType) {
            DisplayType.WEEK -> {
                val weekStart = targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
                val weekEnd = targetDate.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withTimeAtStartOfDay()
                queryJob = QueryListJob(SimpleLog::class.java, {
                    "(start > ${weekStart.millis} AND start < ${weekEnd.millis}) ORDER BY start ASC"
                })
            }
            else -> queryJob = QueryListJob(SimpleLog::class.java, {
                "(start > ${targetDate.millis} AND start < ${targetDate.plusDays(1).millis})"
            })
        }
        executor.execute(queryJob)
        data.clear()
        if (queryJob.result() != null)
            data.addAll(queryJob.result())
        total()
        listeners.forEach { it.onDataChange(data) }
    }

    override fun customQuery(queryPredicate: String): List<SimpleLog> {
        val query = QueryListJob(SimpleLog::class.java, { queryPredicate })
        executor.execute(query)
        return query.result()
    }

    //region Custom impl

    fun total() = data.sumBy { it.duration.toInt() }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(LogStorage::class.java)!!
    }

}