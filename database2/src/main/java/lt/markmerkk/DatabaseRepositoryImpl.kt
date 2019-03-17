package lt.markmerkk

import javafx.beans.binding.BooleanBinding
import lt.markmerkk.entities.Ticket
import lt.markmerkk.migrations.DBMigration
import lt.markmerkk.schema1.tables.Ticket.TICKET
import lt.markmerkk.schema1.tables.records.TicketRecord
import org.jooq.Record
import org.jooq.Record1
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory

class DatabaseRepositoryImpl(
        private val connProvider: DBConnProvider,
        private val migrations: List<DBMigration>
) : DatabaseRepository {

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
    }

    override fun ticketByRemoteId(remoteId: Long): Ticket? {
        val create = DSL.using(connProvider.connect(), SQLDialect.SQLITE)
        val ticketRecord = create.select()
                .from(TICKET)
                .where(TICKET.REMOTE_ID.eq(remoteId))
                .fetchInto(TICKET)
        return null
    }

    override fun updateTicket(oldticket: Ticket, newTicket: Ticket) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun markTicketAsError(ticket: Ticket) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadTickets(): List<Ticket> {
        val create = DSL.using(connProvider.connect(), SQLDialect.SQLITE)
        val dbResult: Result<TicketRecord> = create
                .select()
                .from(TICKET)
                .fetchInto(TICKET)
        val tickets = dbResult
                .map {
                    //                    Ticket(
//                            _id = it.id.toLong(),
//                            id = it.id.toLong(),
//                            code = it.code,
//                            description = it.description
//                    )
                }
        return emptyList()
    }

    override fun insertTicket(ticket: Ticket) {
        val dbDsl = DSL.using(connProvider.connect(), SQLDialect.SQLITE)
//        val dbResult = dbDsl
//                .insertInto(TICKET)
//                .columns(TICKET.CODE, TICKET.DESCRIPTION)
//                .values(ticket.code, ticket.description)
//                .execute()
    }

    override fun insertOrUpdate(ticket: Ticket): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val logger = LoggerFactory.getLogger("DB")!!
    }

}