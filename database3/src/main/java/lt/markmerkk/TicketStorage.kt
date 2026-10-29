package lt.markmerkk

import lt.markmerkk.db.WT4Database
import lt.markmerkk.entities.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Single
import lt.markmerkk.db.Ticket as TicketRow

class TicketStorage(
        private val database: WT4Database,
        private val timeProvider: TimeProvider
) {

    private val ticketQueries = database.ticketQueries
    private val ticketStatusQueries = database.ticketStatusQueries
    private val ticketUseHistoryQueries = database.ticketUseHistoryQueries

    fun enabledStatuses(): Single<List<String>> {
        return Single.defer {
            val enabledStatuses: List<String> = ticketStatusQueries.selectAll()
                    .executeAsList()
                    .filter { it.enabled }
                    .map { it.name }
            Single.just(enabledStatuses)
        }
    }

    /**
     * Loads tickets with enabled filter
     * Filter includes statuses + user settings
     * Note: If not statuses available, all tickets are loaded
     */
    fun loadFilteredTickets(
            userSettings: UserSettings
    ): Single<List<Ticket>> {
        val userName = userSettings.jiraUser().name
        return enabledStatuses()
                .flatMap { statuses ->
                    if (statuses.isEmpty()) {
                        loadTickets()
                    } else {
                        loadTickets()
                                .map { tickets -> tickets.filter { ticket -> statuses.contains(ticket.status) } }
                    }
                }
                .map { tickets ->
                    tickets.filter { ticket ->
                        (userSettings.ticketFilterIncludeAssignee && ticket.assigneeName == userName)
                                || (userSettings.ticketFilterIncludeReporter && ticket.reporterName == userName)
                                || (userSettings.ticketFilterIncludeIsWatching && ticket.isWatching)
                    }
                }
    }

    fun loadTickets(): Single<List<Ticket>> {
        return Single.defer {
            val tickets = ticketQueries.selectAll()
                    .executeAsList()
                    .map { it.toTicket() }
            Single.just(tickets)
        }
    }

    fun loadTicketStatuses(): Single<List<TicketStatus>> {
        return Single.defer {
            val ticketStatuses = ticketStatusQueries.selectAll()
                    .executeAsList()
                    .map { TicketStatus(name = it.name, enabled = it.enabled) }
            Single.just(ticketStatuses)
        }
    }

    fun refreshTicketStatuses(ticketStatusesNames: Set<String>): Single<Int> {
        return Single.defer {
            database.transaction {
                val newStatusNamesWithValues = ticketStatusesNames
                        .map { TicketStatus(it, isTicketStatusEnabled(it)) }
                ticketStatusQueries.deleteAll()
                newStatusNamesWithValues.forEach { ticketStatus ->
                    ticketStatusQueries.insert(
                            name = ticketStatus.name,
                            enabled = ticketStatus.enabled
                    )
                }
            }
            Single.just(0)
        }
    }

    fun updateTicketStatuses(ticketStatuses: List<TicketStatus>): Single<Int> {
        return Single.defer {
            database.transaction {
                ticketStatuses
                        .filter { isTicketStatusExist(it.name) }
                        .forEach { ticketStatus ->
                            ticketStatusQueries.updateEnabled(
                                    enabled = ticketStatus.enabled,
                                    name = ticketStatus.name
                            )
                        }
            }
            Single.just(0)
        }
    }

    private fun isTicketStatusExist(ticketStatus: String): Boolean {
        return ticketStatusQueries.countByName(ticketStatus).executeAsOne() > 0
    }

    fun isTicketStatusEnabled(ticketStatus: String): Boolean {
        return ticketStatusQueries.selectByName(ticketStatus)
                .executeAsList()
                .firstOrNull()
                ?.enabled ?: true
    }

    fun refreshTicketStatusesSync(ticketStatuses: Set<String>): Int {
        return refreshTicketStatuses(ticketStatuses).toBlocking().value()
    }

    /**
     * Inserts a new ticket or updates the existing one by its remote id
     * @return count of changed rows
     */
    fun insertOrUpdate(ticket: Ticket): Single<Int> {
        return Single.defer {
            val remoteData: RemoteData = ticket.remoteData ?: RemoteData.asEmpty()
            val result = database.transactionWithResult {
                if (isTicketExist(ticket)) {
                    ticketQueries.updateByRemoteId(
                            code = ticket.code.code,
                            code_project = ticket.code.codeProject,
                            code_number = ticket.code.codeNumber,
                            description = ticket.description,
                            parent_id = ticket.parentId,
                            is_deleted = remoteData.isDeleted,
                            is_dirty = remoteData.isDirty,
                            is_error = remoteData.isError,
                            error_message = remoteData.errorMessage,
                            fetchTime = remoteData.fetchTime,
                            URL = remoteData.url,
                            status = ticket.status,
                            assignee = ticket.assigneeName,
                            reporter = ticket.reporterName,
                            is_watching = ticket.isWatching,
                            parent_code = ticket.parentCode.code,
                            remote_id = remoteData.remoteId
                    )
                } else {
                    ticketQueries.insert(
                            code = ticket.code.code,
                            code_project = ticket.code.codeProject,
                            code_number = ticket.code.codeNumber,
                            description = ticket.description,
                            parent_id = ticket.parentId,
                            remote_id = remoteData.remoteId,
                            is_deleted = remoteData.isDeleted,
                            is_dirty = remoteData.isDirty,
                            is_error = remoteData.isError,
                            error_message = remoteData.errorMessage,
                            fetchTime = remoteData.fetchTime,
                            URL = remoteData.url,
                            status = ticket.status,
                            assignee = ticket.assigneeName,
                            reporter = ticket.reporterName,
                            is_watching = ticket.isWatching,
                            parent_code = ticket.parentCode.code
                    )
                }
                ticketQueries.rowsChanged().executeAsOne().toInt()
            }
            Single.just(result)
        }
    }

    fun insertOrUpdateSync(ticket: Ticket): Int {
        return insertOrUpdate(ticket).toBlocking().value()
    }

    fun findTicketsByCode(inputCode: String): Single<List<Ticket>> {
        return Single.defer {
            val tickets = ticketQueries.selectByCode(inputCode)
                    .executeAsList()
                    .map { it.toTicket() }
            Single.just(tickets)
        }
    }

    fun fetchRecentTickets(limit: Int): Single<List<TicketUseHistory>> {
        return Single.defer {
            val recentTickets = ticketUseHistoryQueries.selectRecent(limit.toLong())
                    .executeAsList()
                    .map { ticketRecord ->
                        TicketUseHistory(
                                code = TicketCode.new(ticketRecord.code),
                                description = "",
                                lastUsed = timeProvider.preciseDateTime(ticketRecord.lastUsed)
                        )
                    }
            Single.just(recentTickets)
        }
    }

    fun saveTicketAsUsedSync(
            now: DateTime,
            ticketCode: TicketCode
    ) {
        if (!ticketCode.isEmpty()) {
            database.transaction {
                ticketUseHistoryQueries.deleteByCode(ticketCode.code)
                ticketUseHistoryQueries.insert(
                        code = ticketCode.code,
                        code_project = ticketCode.codeProject,
                        code_number = ticketCode.codeNumber,
                        lastUsed = timeProvider.preciseMillis(now)
                )
            }
        }
    }

    private fun isTicketExist(ticket: Ticket): Boolean {
        val remoteId = ticket.remoteData?.remoteId ?: Const.NO_ID
        if (remoteId == Const.NO_ID) {
            return false
        }
        return ticketQueries.countByRemoteId(remoteId).executeAsOne() > 0
    }

    private fun TicketRow.toTicket(): Ticket {
        return Ticket(
                id = id,
                code = TicketCode.new(code),
                description = description,
                parentId = parent_id,
                status = status,
                assigneeName = assignee,
                reporterName = reporter,
                isWatching = is_watching,
                parentCode = TicketCode.new(parent_code),
                remoteData = RemoteData.new(
                        isDeleted = is_deleted,
                        isDirty = is_dirty,
                        isError = is_error,
                        errorMessage = error_message,
                        fetchTime = fetchTime,
                        url = URL
                )
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}
