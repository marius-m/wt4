package lt.markmerkk

import lt.markmerkk.entities.Ticket
import lt.markmerkk.migrations.DBMigration
import lt.markmerkk.schema1.tables.Ticket.TICKET
import lt.markmerkk.schema1.tables.records.TicketRecord
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
//                    DBUtils.renewUserVersion(conn, it.migrateVersionTo) // todo: temporary measure
                }
            }
            logger.debug("Migrations finished")
        } finally {
            conn.close()
        }
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

    companion object {
        private val logger = LoggerFactory.getLogger("DB")!!
    }

}