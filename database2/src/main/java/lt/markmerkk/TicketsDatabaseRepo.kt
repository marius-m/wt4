package lt.markmerkk

import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.migrations.DBMigration
import lt.markmerkk.schema1.tables.Ticket.TICKET
import lt.markmerkk.schema1.tables.records.TicketRecord
import org.jooq.DSLContext
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import rx.Single

class TicketsDatabaseRepo(
        private val connProvider: DBConnProvider,
        private val migrations: List<DBMigration>
) {

    private val dslContext: DSLContext

    init {
        val conn = connProvider.connect()
        try {
            val dbVersion = DBUtils.userVersion(conn)
            logger.debug("Migrations start")
            logger.debug("Current db version - ${dbVersion}")
            migrations.forEach {
                if (it.needMigration(dbVersion)) {
                    logger.debug("Migrating from ${it.migrateVersionFrom} .. ${it.migrateVersionTo}")
                    it.migrate(conn)
                    logger.debug("Moving DB version to ${it.migrateVersionTo}")
                    DBUtils.renewUserVersion(conn, it.migrateVersionTo)
                }
            }
            logger.debug("Migrations finished")
        } finally {
            conn.close()
        }
        dslContext = DSL.using(connProvider.connect(), SQLDialect.SQLITE)
    }

    fun loadTickets(): Single<List<Ticket>> {
        return Single.defer {
            val dbResult: Result<TicketRecord> = dslContext
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
                                remoteData = RemoteData.new(
                                        remoteId = ticket.remoteId,
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

    fun insertOrUpdate(ticket: Ticket): Single<Int> {
        return Single.defer {
            val isTicketExist = isTicketExist(dslContext, ticket)
            val remoteData: RemoteData = ticket.remoteData ?: RemoteData.asEmpty()
            val result = if (isTicketExist) {
                dslContext.update(TICKET)
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
                        .where(TICKET.REMOTE_ID.eq(remoteData.remoteId))
                        .execute()
            } else {
                dslContext.insertInto(
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
                        TICKET.URL
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
                        remoteData.url
                ).execute()
            }
            Single.just(result)
        }
    }

    fun findTicketsByCode(inputCode: String): Single<List<Ticket>> {
        return Single.defer {
            val tickets = dslContext.select()
                    .from(TICKET)
                    .where(TICKET.CODE.eq(inputCode))
                    .fetchInto(TICKET)
                    .map { ticket ->
                        Ticket(
                                id = ticket.id.toLong(),
                                code = TicketCode.new(ticket.code),
                                description = ticket.description,
                                parentId = ticket.parentId,
                                remoteData = RemoteData.new(
                                        remoteId = ticket.remoteId,
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