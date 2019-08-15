package lt.markmerkk.tickets

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.TicketStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import org.joda.time.DateTime
import rx.Observable
import rx.Single

/**
 * This probably should be renamed as 'Api' as it would be more explicit
 */
class TicketApi(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraTicketSearch: JiraTicketSearch,
        private val ticketsDatabaseRepo: TicketStorage,
        private val userSettings: UserSettings
) {

    fun searchRemoteTicketsAndCache(
            now: DateTime
    ): Single<List<Ticket>> {
        return jiraClientProvider.clientStream()
                .flatMapObservable { jiraTicketSearch.searchIssues(now, it, userSettings.issueJql) }
                .doOnNext { ticketsDatabaseRepo.insertOrUpdate(it).subscribe() }
                .toList()
                .flatMapSingle { ticketsDatabaseRepo.loadTickets() }
                .toSingle()
    }

}