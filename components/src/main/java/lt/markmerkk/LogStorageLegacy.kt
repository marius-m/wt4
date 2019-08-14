package lt.markmerkk

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 12/13/15.
 * Represents the storage for simple use.
 */
@Deprecated("Old database access, not used any more")
class LogStorageLegacy(
//        private var executor: IExecutor,
        private val worklogRepository: WorklogRepository,
        private val timeProvider: TimeProvider
) : IDataStorage<SimpleLog> {

    override var data = mutableListOf<SimpleLog>()

    private var listeners = mutableListOf<IDataListener<SimpleLog>>()
    var displayType = DisplayTypeLength.DAY
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
//        executor.execute(InsertJob(SimpleLog::class.java, dataEntity))
//        notifyDataChange()
    }

    override fun delete(dataEntity: SimpleLog) {
//        executor.execute(DeleteJob(SimpleLog::class.java, dataEntity))
//        notifyDataChange()
    }

    override fun update(dataEntity: SimpleLog) {
//        executor.execute(UpdateJob(SimpleLog::class.java, dataEntity))
//        notifyDataChange()
    }

    override fun findByIdOrNull(id: Long): SimpleLog? {
//        val queryJob = QueryListJob<SimpleLog>(
//                SimpleLog::class.java,
//                { "_id = " + id }
//        )
//        executor.execute(queryJob)
//        return queryJob.result().firstOrNull()
        throw UnsupportedOperationException()
    }

    override fun notifyDataChange() {
//        val queryJob: QueryListJob<SimpleLog>
//        when (displayType) {
//            DisplayTypeLength.WEEK -> {
//                val weekStart = targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
//                val weekEnd = targetDate.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withTimeAtStartOfDay()
//                queryJob = QueryListJob(SimpleLog::class.java, {
//                    "(start > ${weekStart.millis} AND start < ${weekEnd.millis}) ORDER BY start ASC"
//                })
//            }
//            else -> queryJob = QueryListJob(SimpleLog::class.java, {
//                "(start > ${targetDate.millis} AND start < ${targetDate.plusDays(1).millis})"
//            })
//        }
//        executor.execute(queryJob)
//        data.clear()
//        if (queryJob.result() != null)
//            data.addAll(queryJob.result())
//        total()
//        listeners.forEach { it.onDataChange(data) }
    }

    //region Custom impl

    fun total() = data.sumBy { it.duration.toInt() }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(LogStorage::class.java)!!
    }

}
