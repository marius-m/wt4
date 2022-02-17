package lt.markmerkk

import lt.markmerkk.entities.Log
import lt.markmerkk.events.EventActiveDisplayDataChange
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.Duration
import org.slf4j.LoggerFactory

class LogRepositoryDefault(
    private val worklogStorage: WorklogStorage,
    private val timeProvider: TimeProvider,
    private val autoSyncWatcher: AutoSyncWatcher2,
    private val eventBus: WTEventBus
) : LogRepository {

    private var _data = emptyList<Log>()
    override val data: List<Log>
        get() = _data

    private var _displayType = DisplayTypeLength.DAY
        set(value) {
            field = value
            notifyDataChange()
        }
    override val displayType: DisplayTypeLength
        get() = _displayType

    private var _targetDate = DateTime().withTime(0, 0, 0, 0)
        set(value) {
            field = value.withTime(0, 0, 0, 0)
            notifyDataChange()
        }
    override val targetDate: DateTime
        get() = _targetDate

    init {
        notifyDataChange()
    }

    override fun changeDisplayType(displayType: DisplayTypeLength) {
        this._displayType = displayType
    }

    override fun changeActiveDate(newActiveDate: DateTime) {
        this._targetDate = newActiveDate
    }

    override fun insertOrUpdate(log: Log): Long {
        val newId = worklogStorage.insertOrUpdateSync(log)
        autoSyncWatcher.markForShortCycleUpdate()
        notifyDataChange()
        return newId
    }

    override fun delete(log: Log): Long {
        val newId = worklogStorage.deleteSync(log)
        autoSyncWatcher.markForShortCycleUpdate()
        notifyDataChange()
        return newId
    }

    override fun update(log: Log): Long {
        val newId = worklogStorage.updateSync(log)
        autoSyncWatcher.markForShortCycleUpdate()
        notifyDataChange()
        return newId
    }

    override fun findByIdOrNull(id: Long): Log? {
        return worklogStorage.findById(id)
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
        _data = worklogStorage.loadWorklogsSync(fromDate, toDate)
                .filter { !it.isMarkedForDeletion }
        eventBus.post(EventActiveDisplayDataChange(data))
    }

    override fun totalInMillis() = data.fold(0L) { sum, log ->
        sum + log.time.duration.millis
    }

    override fun totalAsDuration(): Duration {
        return Duration(totalInMillis())
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}
