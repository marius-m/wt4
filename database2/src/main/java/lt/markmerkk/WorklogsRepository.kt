package lt.markmerkk

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.schema1.Tables.WORKLOG
import lt.markmerkk.schema1.tables.records.WorklogRecord
import org.joda.time.LocalDate
import org.jooq.Result
import org.slf4j.LoggerFactory
import rx.Single
import java.util.logging.LogRecord

class WorklogsRepository(
        private val connProvider: DBConnProvider,
        private val timeProvider: TimeProvider
) {

    fun loadWorklogs(
            from: LocalDate,
            to: LocalDate
    ): Single<List<Log>> {
        return Single.defer {
            val dbResult: Result<WorklogRecord> = connProvider.dsl
                    .select()
                    .from(WORKLOG)
                    .fetchInto(WORKLOG)
            val worklogs = dbResult
                    .map { worklog ->
                        Log.fromDatabase(
                                timeProvider = timeProvider,
                                id = worklog.id.toLong(),
                                start = worklog.start,
                                end = worklog.end,
                                code = worklog.code,
                                comment = worklog.comment,
                                remoteData = RemoteData.new(
                                        remoteId = worklog.remoteId,
                                        isDeleted = worklog.isDeleted.toBoolean(),
                                        isDirty = worklog.isDirty.toBoolean(),
                                        isError = worklog.isError.toBoolean(),
                                        errorMessage = worklog.errorMessage,
                                        fetchTime = worklog.fetchtime,
                                        url = worklog.url
                                )
                        )
                    }
            Single.just(worklogs)
        }
    }

    fun insertOrUpdate(log: Log) {

    }

    fun insertOrUpdateRemoteEntry(log: Log) {

    }

    fun update(log: Log) {

    }

    fun findByRemoteId(remoteId: Long): Log? {
        return null
    }

    fun findById(remoteId: Long): Log? {
        return null
    }

    fun delete(localId: Long) {

    }

//    fun insertOrUpdate(ticket: Ticket): Single<Int> {
//        return Single.defer {
//            val isTicketExist = isTicketExist(connProvider.dsl, ticket)
//            val remoteData: RemoteData = ticket.remoteData ?: RemoteData.asEmpty()
//            val result = if (isTicketExist) {
//                connProvider.dsl.update(TICKET)
//                        .set(TICKET.CODE, ticket.code.code)
//                        .set(TICKET.CODE_PROJECT, ticket.code.codeProject)
//                        .set(TICKET.CODE_NUMBER, ticket.code.codeNumber)
//                        .set(TICKET.DESCRIPTION, ticket.description)
//                        .set(TICKET.PARENT_ID, ticket.parentId)
//                        .set(TICKET.REMOTE_ID, remoteData.remoteId)
//                        .set(TICKET.IS_DELETED, remoteData.isDeleted.toByte())
//                        .set(TICKET.IS_DIRTY, remoteData.isDirty.toByte())
//                        .set(TICKET.IS_ERROR, remoteData.isError.toByte())
//                        .set(TICKET.ERROR_MESSAGE, remoteData.errorMessage)
//                        .set(TICKET.FETCHTIME, remoteData.fetchTime)
//                        .set(TICKET.URL, remoteData.url)
//                        .where(TICKET.REMOTE_ID.eq(remoteData.remoteId))
//                        .execute()
//            } else {
//                connProvider.dsl.insertInto(
//                        TICKET,
//                        TICKET.CODE,
//                        TICKET.CODE_PROJECT,
//                        TICKET.CODE_NUMBER,
//                        TICKET.DESCRIPTION,
//                        TICKET.PARENT_ID,
//                        TICKET.REMOTE_ID,
//                        TICKET.IS_DELETED,
//                        TICKET.IS_DIRTY,
//                        TICKET.IS_ERROR,
//                        TICKET.ERROR_MESSAGE,
//                        TICKET.FETCHTIME,
//                        TICKET.URL
//                ).values(
//                        ticket.code.code,
//                        ticket.code.codeProject,
//                        ticket.code.codeNumber,
//                        ticket.description,
//                        ticket.parentId,
//                        remoteData.remoteId,
//                        remoteData.isDeleted.toByte(),
//                        remoteData.isDirty.toByte(),
//                        remoteData.isError.toByte(),
//                        remoteData.errorMessage,
//                        remoteData.fetchTime,
//                        remoteData.url
//                ).execute()
//            }
//            Single.just(result)
//        }
//    }
//
//    fun findTicketsByCode(inputCode: String): Single<List<Ticket>> {
//        return Single.defer {
//            val tickets = connProvider.dsl.select()
//                    .from(TICKET)
//                    .where(TICKET.CODE.eq(inputCode))
//                    .fetchInto(TICKET)
//                    .map { ticket ->
//                        Ticket(
//                                id = ticket.id.toLong(),
//                                code = TicketCode.new(ticket.code),
//                                description = ticket.description,
//                                parentId = ticket.parentId,
//                                remoteData = RemoteData.new(
//                                        remoteId = ticket.remoteId,
//                                        isDeleted = ticket.isDeleted.toBoolean(),
//                                        isDirty = ticket.isDirty.toBoolean(),
//                                        isError = ticket.isError.toBoolean(),
//                                        errorMessage = ticket.errorMessage,
//                                        fetchTime = ticket.fetchtime,
//                                        url = ticket.url
//                                )
//                        )
//                    }
//            Single.just(tickets)
//        }
//    }
//
//    private fun isTicketExist(dslContext: DSLContext, ticket: Ticket): Boolean {
//        val remoteId = ticket.remoteData?.remoteId ?: Const.NO_ID
//        if (remoteId == Const.NO_ID) {
//            return false
//        }
//        val ticketCount = dslContext.selectCount()
//                .from(TICKET)
//                .where(TICKET.REMOTE_ID.eq(remoteId))
//                .fetchOne(0, Integer::class.java)
//        return ticketCount > 0
//    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}