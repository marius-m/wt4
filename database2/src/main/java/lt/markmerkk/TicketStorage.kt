package lt.markmerkk

import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TicketStatus
import lt.markmerkk.schema1.tables.Ticket.TICKET
import lt.markmerkk.schema1.tables.TicketStatus.TICKET_STATUS
import lt.markmerkk.schema1.tables.records.TicketRecord
import org.jooq.DSLContext
import org.jooq.Result
import org.slf4j.LoggerFactory
import rx.Single

class TicketStorage(
        private val connProvider: DBConnProvider
) {

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

    fun refreshTicketStatuses(ticketStatuses: List<TicketStatus>): Single<Int> {
        return Single.defer {
            val newRecordStep = ticketStatuses
                    .map {
                        connProvider.dsl.insertInto(
                                TICKET_STATUS,
                                TICKET_STATUS.NAME
                        ).values(it.name)
                    }
            connProvider.dsl
                    .deleteFrom(TICKET_STATUS)
                    .execute()
            connProvider.dsl
                    .batch(newRecordStep)
                    .execute()
            Single.just(0)
        }
    }

    fun refreshTicketStatusesSync(ticketStatuses: List<TicketStatus>): Int {
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