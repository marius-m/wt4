package lt.markmerkk.widgets.log_check

import lt.markmerkk.TimeProvider
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import org.joda.time.LocalDate

/**
 * Responsible for checking if all the logs are up-to-date
 */
class LogFreshnessChecker(
        private val worklogStorage: WorklogStorage,
        private val timeProvider: TimeProvider
) {
    fun firstDayOfMonth(): LocalDate = timeProvider.now()
            .toLocalDate()
            .withDayOfMonth(1)
    fun lastDayOfMonth(): LocalDate = firstDayOfMonth()
            .plusMonths(1)
            .minusDays(1)

    fun unSynchronizedLogs(): List<Log> {
        val localWorklogs = worklogStorage.loadWorklogsSync(
                from = firstDayOfMonth(),
                to = lastDayOfMonth()
        ).filter { !it.isRemote }
        return localWorklogs
    }

    fun isUpToDate(): Boolean {
        return unSynchronizedLogs().count() <= 0
    }

}