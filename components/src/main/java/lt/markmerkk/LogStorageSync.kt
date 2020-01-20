package lt.markmerkk

import lt.markmerkk.entities.SimpleLog
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.Duration
import org.slf4j.LoggerFactory

/**
 * Synchronous storage
 */
@Deprecated("Should be replaced with WorklogStorage as it serves the same purpose")
class LogStorage(
        private val worklogStorage: WorklogStorage,
        private val timeProvider: TimeProvider,
        private val autoSyncWatcher: AutoSyncWatcher2
) : IDataStorage<SimpleLog> {

    override var data = emptyList<SimpleLog>()

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

    override fun register(listener: IDataListener<SimpleLog>) {
        listeners.add(listener)
    }

    override fun unregister(listener: IDataListener<SimpleLog>) {
        listeners.remove(listener)
    }

    override fun insert(dataEntity: SimpleLog): Int {
        val log = dataEntity.toLog(timeProvider)
        val newId = worklogStorage.insertOrUpdateSync(log)
        autoSyncWatcher.markForShortCycleUpdate()
        notifyDataChange()
        return newId
    }

    override fun delete(dataEntity: SimpleLog): Int {
        val log = dataEntity.toLog(timeProvider)
        val newId = worklogStorage.deleteSync(log)
        autoSyncWatcher.markForShortCycleUpdate()
        notifyDataChange()
        return newId
    }

    override fun update(dataEntity: SimpleLog): Int {
        val log = dataEntity.toLog(timeProvider)
        val newId = worklogStorage.updateSync(log)
        autoSyncWatcher.markForShortCycleUpdate()
        notifyDataChange()
        return newId
    }

    override fun findByIdOrNull(id: Long): SimpleLog? {
        return worklogStorage.findById(id)
                ?.toLegacyLog(timeProvider)
    }

    override fun notifyDataChange() {
        val fromDate = when (displayType) {
            DisplayTypeLength.DAY -> targetDate.toLocalDate()
            DisplayTypeLength.WEEK -> targetDate.withDayOfWeek(DateTimeConstants.MONDAY)
                    .withTimeAtStartOfDay()
                    .toLocalDate()
        }
        val toDate = when (displayType) {
            DisplayTypeLength.DAY -> targetDate.plusDays(1).toLocalDate()
            DisplayTypeLength.WEEK -> targetDate.withDayOfWeek(DateTimeConstants.SUNDAY)
                    .plusDays(1)
                    .withTimeAtStartOfDay()
                    .toLocalDate()
        }
        data = worklogStorage.loadWorklogsSync(fromDate, toDate)
                .filter { !it.isMarkedForDeletion }
                .map { it.toLegacyLog(timeProvider) }
        listeners.forEach { it.onDataChange(data) }
    }

    fun total() = data.sumBy { it.duration.toInt() }

    fun totalAsDuration() = Duration(total().toLong())

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}
