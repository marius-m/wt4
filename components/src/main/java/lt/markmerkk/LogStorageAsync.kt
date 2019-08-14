package lt.markmerkk

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.SimpleLog
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.slf4j.LoggerFactory
import rx.Single

/**
 * Controls inner storage in asynchronous way
 */
class LogStorageAsync(
        private val worklogRepository: WorklogRepository,
        private val timeProvider: TimeProvider,
        private val schedulerProvider: SchedulerProvider
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

    override fun insert(dataEntity: SimpleLog) {
        val log = dataEntity.toLog(timeProvider)
        worklogRepository.insertOrUpdate(log)
                .flatMap { activeWorklogs() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ logs ->
                    data = logs
                            .map { it.toLegacyLog(timeProvider) }
                    listeners.forEach { it.onDataChange(data) }
                }, {
                    data = emptyList()
                    listeners.forEach { it.onDataChange(data) }
                })
    }

    override fun delete(dataEntity: SimpleLog) {
        val log = dataEntity.toLog(timeProvider)
        worklogRepository.delete(log.id)
                .flatMap { activeWorklogs() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ logs ->
                    data = logs
                            .map { it.toLegacyLog(timeProvider) }
                    listeners.forEach { it.onDataChange(data) }
                }, {
                    data = emptyList()
                    listeners.forEach { it.onDataChange(data) }
                })
    }

    override fun update(dataEntity: SimpleLog) {
        val log = dataEntity.toLog(timeProvider)
        worklogRepository.update(log)
                .flatMap { activeWorklogs() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ logs ->
                    data = logs
                            .map { it.toLegacyLog(timeProvider) }
                    listeners.forEach { it.onDataChange(data) }
                }, {
                    data = emptyList()
                    listeners.forEach { it.onDataChange(data) }
                })
    }

    override fun findByIdOrNull(id: Long): SimpleLog? {
        return worklogRepository.findById(id)
                ?.toLegacyLog(timeProvider)
    }

    override fun notifyDataChange() {
        activeWorklogs()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ logs ->
                    data = logs
                            .map { it.toLegacyLog(timeProvider) }
                    listeners.forEach { it.onDataChange(data) }
                }, {
                    data = emptyList()
                    listeners.forEach { it.onDataChange(data) }
                })
    }

    private fun activeWorklogs(): Single<List<Log>> {
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
        return worklogRepository.loadWorklogs(fromDate, toDate)
    }

    fun total() = data.sumBy { it.duration.toInt() }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}
