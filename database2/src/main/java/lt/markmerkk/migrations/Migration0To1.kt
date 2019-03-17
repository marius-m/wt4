package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.Worklog
import lt.markmerkk.schema1.Tables.TICKET
import lt.markmerkk.schema1.tables.Worklog.WORKLOG
import lt.markmerkk.toBoolean
import lt.markmerkk.toByte
import org.joda.time.DateTime
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration0To1(
        private val oldDatabase: DBConnProvider
): DBMigration {

    override val migrateVersionFrom: Int = 0
    override val migrateVersionTo: Int = 1

    override fun migrate(conn: Connection) {
        val currentDsl = DSL.using(conn, SQLDialect.SQLITE)
        currentDsl.createTableIfNotExists(TICKET)
                .column(TICKET.ID)
                .column(TICKET.CODE_PROJECT)
                .column(TICKET.CODE_NUMBER)
                .column(TICKET.CODE)
                .column(TICKET.DESCRIPTION)
                .column(TICKET.PARENT_ID)
                .column(TICKET.REMOTE_ID)
                .column(TICKET.IS_DELETED)
                .column(TICKET.IS_DIRTY)
                .column(TICKET.IS_ERROR)
                .column(TICKET.ERROR_MESSAGE)
                .column(TICKET.FETCHTIME)
                .column(TICKET.URL)
                .execute()
        currentDsl.createTableIfNotExists(WORKLOG)
                .column(WORKLOG.ID)
                .column(WORKLOG.START)
                .column(WORKLOG.END)
                .column(WORKLOG.DURATION)
                .column(WORKLOG.CODE)
                .column(WORKLOG.COMMENT)
                .column(WORKLOG.REMOTE_ID)
                .column(WORKLOG.IS_DELETED)
                .column(WORKLOG.IS_DIRTY)
                .column(WORKLOG.IS_ERROR)
                .column(WORKLOG.ERROR_MESSAGE)
                .column(WORKLOG.FETCHTIME)
                .column(WORKLOG.URL)
                .execute()
        moveFromOldDatabase(currentDsl)
    }

    private fun moveFromOldDatabase(currentDsl: DSLContext) {
        val now = DateTime.now().millis
        if (!oldDatabase.exist()) {
            logger.info("No old database")
            return
        }
        logger.info("Migrating old database")
        val oldConn = oldDatabase.connect()
        try {
            val oldDsl = DSL.using(oldConn, SQLDialect.SQLITE)
            moveLocalIssues(oldConn, currentDsl, now)
            moveSingleLog(oldConn, currentDsl, now)
        } catch (e: DataAccessException) {
            logger.warn("Error moving old database", e)
        } finally {
            oldConn.close()
        }
    }

    private fun moveLocalIssues(
            oldConn: Connection,
            currentDsl: DSLContext,
            now: Long
    ) {
        val sql = "SELECT * FROM LocalIssue"
        val oldTickets = mutableListOf<Ticket>()
        try {
            val statement = oldConn.createStatement()
            val rs = statement.executeQuery(sql)
            while (rs.next()) {
                val key = rs.getString("key")
                val description = rs.getString("description")
                val _id = rs.getLong("_id")
                val uri = rs.getString("uri")
                val deleted = rs.getInt("deleted")
                val dirty = rs.getInt("dirty")
                val error = rs.getInt("error")
                val errorMessage: String = rs.getString("errorMessage") ?: ""
                val oldTicket = Ticket.new(
                        code = key,
                        description = description,
                        remoteData = RemoteData.new(
                                remoteId = _id,
                                isDeleted = deleted.toBoolean(),
                                isDirty = dirty.toBoolean(),
                                isError = error.toBoolean(),
                                errorMessage = errorMessage,
                                uri = uri,
                                fetchTime = now
                        )
                )
                oldTickets.add(oldTicket)
            }
        } catch (e: SQLException) {
            logger.warn("Could not read old database", e)
        }
        val issueMigration = oldTickets.map { ticket ->
            val remoteData: RemoteData = ticket.remoteData ?: RemoteData.asEmpty()
            currentDsl
                    .insertInto(TICKET)
                    .columns(
                            TICKET.CODE,
                            TICKET.CODE_PROJECT,
                            TICKET.CODE_NUMBER,
                            TICKET.DESCRIPTION,
                            TICKET.REMOTE_ID,
                            TICKET.IS_DELETED,
                            TICKET.IS_DIRTY,
                            TICKET.IS_ERROR,
                            TICKET.ERROR_MESSAGE,
                            TICKET.FETCHTIME,
                            TICKET.URL
                    )
                    .values(
                            ticket.code.code,
                            ticket.code.codeProject,
                            ticket.code.codeNumber,
                            ticket.description,
                            remoteData.remoteId,
                            remoteData.isDeleted.toByte(),
                            remoteData.isDirty.toByte(),
                            remoteData.isError.toByte(),
                            remoteData.errorMessage,
                            remoteData.fetchTime,
                            remoteData.url
                    )
        }
        currentDsl.batch(issueMigration)
                .execute()
    }

    private fun moveSingleLog(
            oldConnection: Connection,
            currentDsl: DSLContext,
            now: Long
    ) {
        val sql = "SELECT * FROM Log"
        val oldWorklogs = mutableListOf<Worklog>()
        try {
            val statement = oldConnection.createStatement()
            val rs = statement.executeQuery(sql)
            while (rs.next()) {
                val start = rs.getLong("start")
                val end = rs.getLong("end")
                val duration = rs.getLong("duration")
                val task = rs.getString("task")
                val comment = rs.getString("comment")
                val _id = rs.getLong("_id")
                val uri = rs.getString("uri")
                val deleted = rs.getInt("deleted")
                val dirty = rs.getInt("dirty")
                val error = rs.getInt("error")
                val errorMessage: String = rs.getString("errorMessage") ?: ""
                val worklog = Worklog.new(
                        start = start,
                        end = end,
                        duration = duration,
                        code = task,
                        comment = comment,
                        remoteData = RemoteData.new(
                                remoteId = _id,
                                isDeleted = deleted.toBoolean(),
                                isDirty = dirty.toBoolean(),
                                isError = error.toBoolean(),
                                errorMessage = errorMessage,
                                uri = uri,
                                fetchTime = now
                        )
                )
                oldWorklogs.add(worklog)
            }
        } catch (e: SQLException) {
            logger.warn("Could not read old database", e)
        }
        val logMigrationToNewDb = oldWorklogs.map { worklog ->
            val remoteData: RemoteData = worklog.remoteData ?: RemoteData.asEmpty()
            currentDsl
                    .insertInto(WORKLOG)
                    .columns(
                            WORKLOG.START,
                            WORKLOG.END,
                            WORKLOG.DURATION,
                            WORKLOG.CODE,
                            WORKLOG.COMMENT,
                            WORKLOG.REMOTE_ID,
                            WORKLOG.IS_DELETED,
                            WORKLOG.IS_DIRTY,
                            WORKLOG.IS_ERROR,
                            WORKLOG.ERROR_MESSAGE,
                            WORKLOG.FETCHTIME,
                            WORKLOG.URL
                    )
                    .values(
                            worklog.start,
                            worklog.end,
                            worklog.duration,
                            worklog.code,
                            worklog.comment,
                            remoteData.remoteId,
                            remoteData.isDeleted.toByte(),
                            remoteData.isDirty.toByte(),
                            remoteData.isError.toByte(),
                            remoteData.errorMessage,
                            remoteData.fetchTime,
                            remoteData.url
                    )
        }
        currentDsl.batch(logMigrationToNewDb)
                .execute()
    }

    companion object {
        private val logger = LoggerFactory.getLogger("DB")!!
    }
}