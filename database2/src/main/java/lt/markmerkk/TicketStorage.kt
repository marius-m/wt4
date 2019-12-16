package lt.markmerkk

import lt.markmerkk.entities.*
import lt.markmerkk.schema1.Tables.TICKET_USE_HISTORY
import lt.markmerkk.schema1.tables.Ticket.TICKET
import lt.markmerkk.schema1.tables.TicketStatus.TICKET_STATUS
import lt.markmerkk.schema1.tables.records.TicketRecord
import lt.markmerkk.schema1.tables.records.TicketStatusRecord
import org.joda.time.DateTime
import org.jooq.DSLContext
import org.jooq.Result
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Single

class TicketStorage(
        private val connProvider: DBConnProvider,
        private val timeProvider: TimeProvider
) {

    fun enabledStatuses(): Single<List<String>> {
        return Single.defer {
            val enabledStatuses: List<String> = connProvider.dsl
                    .select()
                    .from(TICKET_STATUS)
                    .fetchInto(TICKET_STATUS)
                    .map { TicketStatus(it.name, it.enabled.toBoolean()) }
                    .filter { it.enabled }
                    .map { it.name }
            Single.just(enabledStatuses)
        }
    }

    fun loadTicketsWithEnabledStatus(
            likeString: String
    ): Single<List<Ticket>> {
        return enabledStatuses()
                .flatMap {
                    val dbResult: Result<TicketRecord> = connProvider.dsl
                            .select()
                            .from(TICKET)
                            .where(TICKET.CODE.like(likeString)
                                    .or(TICKET.DESCRIPTION.like(likeString)))
                            .fetchInto(TICKET)
                    val tickets = dbResult
                            .map { ticket ->
                                Ticket(
                                        id = ticket.id.toLong(),
                                        code = TicketCode.new(ticket.code),
                                        description = ticket.description,
                                        parentId = ticket.parentId,
                                        status = ticket.status,
                                        parentCode = TicketCode.new(ticket.parentCode),
                                        remoteData = RemoteData.new(
                                                isDeleted = ticket.isDeleted.toBoolean(),
                                                isDirty = ticket.isDirty.toBoolean(),
                                                isError = ticket.isError.toBoolean(),
                                                errorMessage = ticket.errorMessage,
                                                fetchTime = ticket.fetchtime,
                                                url = ticket.url
                                        )
                                )
                            }
                    Single.just(tickets)
                }
    }

    fun loadTicketsWithEnabledStatuses(): Single<List<Ticket>> {
        return Single.zip(
                enabledStatuses(),
                loadTickets(),
                { statuses, tickets ->
                    tickets.filter {
                        statuses.contains(it.status)
                    }
                }
        )
    }

    fun loadTickets(): Single<List<Ticket>> {
        return Single.defer {
            val dbResult: Result<TicketRecord> = connProvider.dsl
                    .select()
                    .from(TICKET)
                    .fetchInto(TICKET)
            val tickets = dbResult
                    .map { ticket ->
                        Ticket(
                                id = ticket.id.toLong(),
                                code = TicketCode.new(ticket.code),
                                description = ticket.description,
                                parentId = ticket.parentId,
                                status = ticket.status,
                                parentCode = TicketCode.new(ticket.parentCode),
                                remoteData = RemoteData.new(
                                        isDeleted = ticket.isDeleted.toBoolean(),
                                        isDirty = ticket.isDirty.toBoolean(),
                                        isError = ticket.isError.toBoolean(),
                                        errorMessage = ticket.errorMessage,
                                        fetchTime = ticket.fetchtime,
                                        url = ticket.url
                                )
                        )
                    }
            Single.just(tickets)
        }
    }

    fun loadTicketStatuses(): Single<List<TicketStatus>> {
        return Single.defer {
            val dbResult: Result<TicketStatusRecord> = connProvider.dsl
                    .select()
                    .from(TICKET_STATUS)
                    .fetchInto(TICKET_STATUS)
            val ticketStatuses = dbResult
                    .map { TicketStatus(name = it.name, enabled = it.enabled.toBoolean()) }
            Single.just(ticketStatuses)
        }
    }

    fun refreshTicketStatuses(ticketStatusesNames: List<String>): Single<Int> {
        return Single.defer {
            val newStatusNamesWithValues = ticketStatusesNames
                    .map { TicketStatus(it, isTicketStatusEnabled(it)) }
            connProvider.dsl
                    .deleteFrom(TICKET_STATUS)
                    .execute()
            newStatusNamesWithValues
                    .map { ticketStatus ->
                        connProvider.dsl.insertInto(
                                TICKET_STATUS,
                                TICKET_STATUS.NAME,
                                TICKET_STATUS.ENABLED
                        ).values(
                                ticketStatus.name,
                                ticketStatus.enabled.toByte()
                        )
                    }.forEach { it.execute() }
            Single.just(0)
        }
    }

    fun updateTicketStatuses(ticketStatuses: List<TicketStatus>): Single<Int> {
        return Single.defer {
            ticketStatuses
                    .filter {
                        isTicketStatusExist(connProvider.dsl, it.name)
                    }
                    .forEach { ticketStatus ->
                        connProvider.dsl.update(TICKET_STATUS)
                                .set(TICKET_STATUS.NAME, ticketStatus.name)
                                .set(TICKET_STATUS.ENABLED, ticketStatus.enabled.toByte())
                                .where(TICKET_STATUS.NAME.eq(ticketStatus.name))
                                .execute()
                    }
            Single.just(0)
        }
    }

    private fun isTicketStatusExist(dslContext: DSLContext, ticketStatus: String): Boolean {
        val ticketStatusCount = dslContext.selectCount()
                .from(TICKET_STATUS)
                .where(TICKET_STATUS.NAME.eq(ticketStatus))
                .fetchOne(0, Integer::class.java)
        return ticketStatusCount > 0
    }

    fun isTicketStatusEnabled(ticketStatus: String): Boolean {
        return connProvider.dsl
                .select()
                .from(TICKET_STATUS)
                .where(TICKET_STATUS.NAME.eq(ticketStatus))
                .fetchInto(TICKET_STATUS)
                .map { TicketStatus(it.name, it.enabled.toBoolean()) }
                .firstOrNull { it.name == ticketStatus }?.enabled ?: true
    }

    fun refreshTicketStatusesSync(ticketStatuses: List<String>): Int {
        return refreshTicketStatuses(ticketStatuses).toBlocking().value()
    }

    fun insertOrUpdate(ticket: Ticket): Single<Int> {
        return Single.defer {
            val isTicketExist = isTicketExist(connProvider.dsl, ticket)
            val remoteData: RemoteData = ticket.remoteData ?: RemoteData.asEmpty()
            val result = if (isTicketExist) {
                connProvider.dsl.update(TICKET)
                        .set(TICKET.CODE, ticket.code.code)
                        .set(TICKET.CODE_PROJECT, ticket.code.codeProject)
                        .set(TICKET.CODE_NUMBER, ticket.code.codeNumber)
                        .set(TICKET.DESCRIPTION, ticket.description)
                        .set(TICKET.PARENT_ID, ticket.parentId)
                        .set(TICKET.REMOTE_ID, remoteData.remoteId)
                        .set(TICKET.IS_DELETED, remoteData.isDeleted.toByte())
                        .set(TICKET.IS_DIRTY, remoteData.isDirty.toByte())
                        .set(TICKET.IS_ERROR, remoteData.isError.toByte())
                        .set(TICKET.ERROR_MESSAGE, remoteData.errorMessage)
                        .set(TICKET.FETCHTIME, remoteData.fetchTime)
                        .set(TICKET.URL, remoteData.url)
                        .set(TICKET.STATUS, ticket.status)
                        .set(TICKET.PARENT_CODE, ticket.parentCode.code)
                        .where(TICKET.REMOTE_ID.eq(remoteData.remoteId))
                        .execute()
            } else {
                connProvider.dsl.insertInto(
                        TICKET,
                        TICKET.CODE,
                        TICKET.CODE_PROJECT,
                        TICKET.CODE_NUMBER,
                        TICKET.DESCRIPTION,
                        TICKET.PARENT_ID,
                        TICKET.REMOTE_ID,
                        TICKET.IS_DELETED,
                        TICKET.IS_DIRTY,
                        TICKET.IS_ERROR,
                        TICKET.ERROR_MESSAGE,
                        TICKET.FETCHTIME,
                        TICKET.URL,
                        TICKET.STATUS,
                        TICKET.PARENT_CODE
                ).values(
                        ticket.code.code,
                        ticket.code.codeProject,
                        ticket.code.codeNumber,
                        ticket.description,
                        ticket.parentId,
                        remoteData.remoteId,
                        remoteData.isDeleted.toByte(),
                        remoteData.isDirty.toByte(),
                        remoteData.isError.toByte(),
                        remoteData.errorMessage,
                        remoteData.fetchTime,
                        remoteData.url,
                        ticket.status,
                        ticket.parentCode.code
                ).execute()
            }
            Single.just(result)
        }
    }

    fun insertOrUpdateSync(ticket: Ticket): Int {
        return insertOrUpdate(ticket).toBlocking().value()
    }

    fun findTicketsByCode(inputCode: String): Single<List<Ticket>> {
        return Single.defer {
            val tickets = connProvider.dsl.select()
                    .from(TICKET)
                    .where(TICKET.CODE.eq(inputCode))
                    .fetchInto(TICKET)
                    .map { ticket ->
                        Ticket(
                                id = ticket.id.toLong(),
                                code = TicketCode.new(ticket.code),
                                description = ticket.description,
                                parentId = ticket.parentId,
                                status = ticket.status,
                                parentCode = TicketCode.new(ticket.parentCode),
                                remoteData = RemoteData.new(
                                        isDeleted = ticket.isDeleted.toBoolean(),
                                        isDirty = ticket.isDirty.toBoolean(),
                                        isError = ticket.isError.toBoolean(),
                                        errorMessage = ticket.errorMessage,
                                        fetchTime = ticket.fetchtime,
                                        url = ticket.url
                                )
                        )
                    }
            Single.just(tickets)
        }
    }

    fun fetchRecentTickets(limit: Int): Single<List<TicketUseHistory>> {
        return Single.defer {
            val recentTickets = connProvider.dsl.select()
                    .from(TICKET_USE_HISTORY)
                    .orderBy(TICKET_USE_HISTORY.LASTUSED)
                    .limit(limit)
                    .fetchInto(TICKET_USE_HISTORY)
                    .map { ticketRecord ->
                        TicketUseHistory(
                                code = TicketCode.new(ticketRecord.code),
                                description = "",
                                lastUsed = timeProvider.roundDateTime(ticketRecord.lastused)
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
            rmHistoryWithCode(connProvider.dsl, ticketCode)
            connProvider.dsl.insertInto(
                    TICKET_USE_HISTORY,
                    TICKET_USE_HISTORY.CODE,
                    TICKET_USE_HISTORY.CODE_PROJECT,
                    TICKET_USE_HISTORY.CODE_NUMBER,
                    TICKET_USE_HISTORY.LASTUSED
            ).values(
                    ticketCode.code,
                    ticketCode.codeProject,
                    ticketCode.codeNumber,
                    timeProvider.roundMillis(now)
            ).execute()
        }
    }

    private fun rmHistoryWithCode(dslContext: DSLContext, ticketCode: TicketCode): Int {
        if (ticketCode.isEmpty()) {
            return -1
        }
        return dslContext.deleteFrom(TICKET_USE_HISTORY)
                .where(TICKET_USE_HISTORY.CODE.eq(ticketCode.code))
                .execute()
    }

    private fun isTicketExist(dslContext: DSLContext, ticket: Ticket): Boolean {
        val remoteId = ticket.remoteData?.remoteId ?: Const.NO_ID
        if (remoteId == Const.NO_ID) {
            return false
        }
        val ticketCount = dslContext.selectCount()
                .from(TICKET)
                .where(TICKET.REMOTE_ID.eq(remoteId))
                .fetchOne(0, Integer::class.java)
        return ticketCount > 0
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}