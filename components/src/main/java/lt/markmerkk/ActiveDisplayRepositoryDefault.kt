package lt.markmerkk

import lt.markmerkk.entities.DateRange
import lt.markmerkk.entities.Log
import lt.markmerkk.events.EventActiveDisplayDataChange
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory

class ActiveDisplayRepositoryDefault(
    private val worklogStorage: WorklogStorage,
    private val timeProvider: TimeProvider,
    private val autoSyncWatcher: AutoSyncWatcher2,
    private val eventBus: WTEventBus
) : ActiveDisplayRepository {

    private var _displayLogs = emptyList<Log>()
    override val displayLogs: List<Log>
        get() = _displayLogs

    private var _displayType = DisplayTypeLength.DAY
    override val displayType: DisplayTypeLength
        get() = _displayType

    private var _displayDateRange: DateRange = DateRange.byDisplayType(displayType, timeProvider.now().toLocalDate())
    override val displayDateRange: DateRange
        get() = _displayDateRange

    init {
        notifyDataChange()
    }

    override fun changeDisplayType(displayType: DisplayTypeLength) {
        this._displayType = displayType
        this.changeDisplayDate(displayDateRange.selectDate)
        notifyDataChange()
    }

    override fun changeDisplayDate(newDate: LocalDate) {
        this._displayDateRange = DateRange.byDisplayType(
            displayType = displayType,
            localDate = newDate
        )
        notifyDataChange()
    }

    override fun prevDisplayDate() {
        val newDate = when (displayType) {
            DisplayTypeLength.DAY -> displayDateRange.selectDate.minusDays(1)
            DisplayTypeLength.WEEK -> displayDateRange.selectDate.minusDays(7)
        }
        changeDisplayDate(newDate)
    }

    override fun nextDisplayDate() {
        val newDate = when (displayType) {
            DisplayTypeLength.DAY -> displayDateRange.selectDate.plusDays(1)
            DisplayTypeLength.WEEK -> displayDateRange.selectDate.plusDays(7)
        }
        changeDisplayDate(newDate)
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

    override fun notifyDataChange() {
        _displayLogs = worklogStorage
            .loadWorklogsSync(displayDateRange.start, displayDateRange.end.plusDays(1))
            .filter { !it.isMarkedForDeletion }
        eventBus.post(EventActiveDisplayDataChange(displayLogs))
    }

    override fun totalInMillis() = displayLogs.fold(0L) { sum, log ->
        sum + log.time.duration.millis
    }

    override fun totalAsDuration(): Duration {
        return Duration(totalInMillis())
    }

    override fun durationForTargetDate(target: LocalDate): Duration {
        val dateRangeTarget = DateRange.forActiveDay(target)
        return displayLogs.filter { log -> dateRangeTarget.contains(log.time.start.toLocalDate()) }
            .fold(Duration.ZERO) { duration, log -> duration.plus(log.time.duration) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}
