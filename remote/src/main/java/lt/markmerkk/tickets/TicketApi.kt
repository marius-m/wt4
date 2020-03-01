package lt.markmerkk.tickets

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.TicketStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TicketStatus
import org.joda.time.DateTime
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

    fun fetchProjectStatusesAndCache(
            now: DateTime
    ): Single<List<TicketStatus>> {
        return Single.defer { Single.just(jiraClientProvider.client()) }
                .flatMap { jiraTicketSearch.projectStatuses(now, it) }
                .doOnSuccess { ticketsDatabaseRepo.refreshTicketStatuses(it).subscribe() }
                .flatMap { ticketsDatabaseRepo.loadTicketStatuses() }
    }

    fun searchRemoteTicketsAndCache(
            now: DateTime
    ): Single<List<Ticket>> {
        return Single.defer { Single.just(jiraClientProvider.client()) }
                .flatMap { jiraClient ->
                    val localTicketStream = ticketsDatabaseRepo.loadTickets()
                    val remoteTicketStream = jiraTicketSearch.searchIssues(now, jiraClient, userSettings.issueJql)
                            .toList()
                            .toSingle()
                            .map { it.toList() }
                    localTicketStream.zipWith(
                            remoteTicketStream,
                            { localTickets, remoteTickets ->
                                TicketBatch(localTickets, remoteTickets)
                            })
                }
                .doOnSuccess { ticketBatch ->
                    mergeLocalAndRemoteTickets(
                            localTickets = ticketBatch.localTickets,
                            remoteTickets = ticketBatch.remoteTickets
                    )
                }
                .flatMap { ticketsDatabaseRepo.loadTickets() }
    }

    fun mergeLocalAndRemoteTickets(
            localTickets: List<Ticket>,
            remoteTickets: List<Ticket>
    ) {
        val localByCode: Map<TicketCode, Ticket> = localTickets.map { it.code to it }.toMap()
        val remoteByCode: Map<TicketCode, Ticket> = remoteTickets.map { it.code to it }.toMap()
        val noStatusTickets = localByCode
                .filterNot { (ticketCode, ticket) ->
                    remoteByCode.containsKey(ticketCode)
                }.map { (ticketCode, ticket) -> ticket }
        remoteByCode
                .values
                .forEach { ticketsDatabaseRepo.insertOrUpdateSync(it) }
        noStatusTickets
                .map { it.clearStatus() } // Removing old status
                .forEach { ticketsDatabaseRepo.insertOrUpdateSync(it) }
    }

    private data class TicketBatch(
            val localTickets: List<Ticket>,
            val remoteTickets: List<Ticket>
    )

}
