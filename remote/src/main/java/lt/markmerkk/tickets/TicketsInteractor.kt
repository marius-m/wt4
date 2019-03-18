package lt.markmerkk.tickets

import lt.markmerkk.DatabaseRepository
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import org.joda.time.DateTime
import rx.Observable
import rx.Single

class TicketsInteractor(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraTicketSearch: JiraTicketSearch,
        private val databaseRepository: DatabaseRepository,
        private val userSettings: UserSettings
) {

    fun searchRemoteTickets(
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
                .doOnNext { databaseRepository.insertOrUpdate(it) }
                .toList()
                .flatMapSingle { Single.just(databaseRepository.loadTickets()) }
                .toSingle()
    }

}