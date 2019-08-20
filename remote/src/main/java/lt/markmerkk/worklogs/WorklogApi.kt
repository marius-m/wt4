package lt.markmerkk.worklogs

import lt.markmerkk.*
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Completable
import rx.Observable

class WorklogApi(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraWorklogInteractor: JiraWorklogInteractor,
        private val ticketStorage: TicketStorage,
        private val worklogStorage: WorklogStorage,
        private val userSettings: UserSettings
) {

    fun fetchLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        val startFormat = LogFormatters.shortFormatDate.print(start)
        val endFormat = LogFormatters.shortFormatDate.print(end)
        logger.debug("Starting to fetch worklogs in range: $startFormat / $endFormat for current user")
        val jql = "(worklogDate >= \"$startFormat\" && worklogDate <= \"$endFormat\" && worklogAuthor = currentUser())"
        return jiraWorklogInteractor.searchWorlogs(
                fetchTime = fetchTime,
                jql = jql,
                startDate = start,
                endDate = end)
                .onErrorResumeNext { error ->
                    logger.warn("Error fetching remote worklogs", error)
                    Observable.empty()
                }
                .doOnNext { (ticket, worklogs) ->
                    ticketStorage.insertOrUpdateSync(ticket)
                    worklogs.forEach {
                        worklogStorage.insertOrUpdateSync(it)
                    }
                }
                .toList()
                .toCompletable()
    }

    fun deleteMarkedForDeleteLogs(start: LocalDate, end: LocalDate): Completable {
        return worklogStorage.loadWorklogs(start, end)
                .flatMapObservable { Observable.from(it) }
                .filter { it.isMarkedForDeletion }
                .flatMapSingle { jiraWorklogInteractor.delete(it) }
                .flatMapSingle { worklogStorage.hardDeleteRemote(it) }
                .toList()
                .toCompletable()
    }

    fun uploadLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        return worklogStorage.loadWorklogs(start, end)
                .flatMapObservable { Observable.from(it) }
                .filter { it.canUpload }
                .flatMapSingle { jiraWorklogInteractor.uploadWorklog(fetchTime, it) }
                .flatMapSingle { worklogStorage.insertOrUpdate(it) }
                .toList()
                .toCompletable()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }


}