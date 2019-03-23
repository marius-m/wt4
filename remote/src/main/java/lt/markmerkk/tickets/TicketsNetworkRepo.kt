package lt.markmerkk.tickets

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import org.joda.time.DateTime
import rx.Observable
import rx.Single

class TicketsNetworkRepo(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraTicketSearch: JiraTicketSearch,
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val userSettings: UserSettings
) {

    fun searchRemoteTicketsAndCache(
            now: DateTime
    ): Single<List<Ticket>> {
        return jiraClientProvider.clientStream()
                .flatMapObservable { jiraTicketSearch.searchIssues(it, userSettings.issueJql) }
                .flatMap { Observable.from(it) }
                .map {
                    Ticket.fromRemoteData(
                            code = it.key,
                            description = it.summary,
                            remoteData = RemoteData.fromRemote(
                                    remoteIdUrl = it.id,
                                    fetchTime = now.millis,
                                    url = it.url
                            )
                    )
                }
                .doOnNext { ticketsDatabaseRepo.insertOrUpdate(it).subscribe() }
                .toList()
                .flatMapSingle { ticketsDatabaseRepo.loadTickets() }
                .toSingle()
    }

}