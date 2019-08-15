package lt.markmerkk.worklogs

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.TicketStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.LocalDate
import rx.Single

class WorklogApi(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraWorklogSearch: JiraWorklogSearch,
        private val ticketStorage: TicketStorage,
        private val worklogStorage: WorklogStorage,
        private val userSettings: UserSettings
) {

    fun fetchAndCacheLogs(
            now: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Single<List<Log>> {
        val startFormat = LogFormatters.shortFormatDate.print(start)
        val endFormat = LogFormatters.shortFormatDate.print(end)
        val jql = "(worklogDate >= \"$startFormat\" && worklogDate <= \"$endFormat\" && worklogAuthor = currentUser())"
        return jiraClientProvider.clientStream()
                .flatMapObservable {
                    jiraWorklogSearch.searchWorlogs(
                            now = now,
                            jiraClient = it,
                            jql = jql,
                            startDate = start,
                            endDate = end
                    )
                }.doOnNext { (ticket, worklogs) ->
                    ticketStorage.insertOrUpdateSync(ticket)
                    worklogs.forEach {
                        worklogStorage.insertOrUpdateSync(it)
                    }
                }.toList()
                .flatMapSingle { worklogStorage.loadWorklogs(start, end) }
                .toSingle()
    }

}