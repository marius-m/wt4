package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.toBoolean
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration0To1(
        private val oldDatabase: DBConnProvider,
        private val newDatabase: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 0
    override val migrateVersionTo: Int = 1

    override fun migrate(conn: Connection) {
        val sql = "" +
                "CREATE TABLE IF NOT EXISTS `ticket` ( \n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "  `code_project` VARCHAR(50) DEFAULT '' NOT NULL,\n" +
                "  `code_number` VARCHAR(50) DEFAULT '' NOT NULL,\n" +
                "  `code` VARCHAR(50) DEFAULT '' NOT NULL, \n" +
                "  `description` CLOB DEFAULT '' NOT NULL, \n" +
                "  `parent_id` BIGINT NOT NULL DEFAULT -1, \n" +
                "  `remote_id` BIGINT NOT NULL DEFAULT -1, \n" +
                "  `is_deleted` TINYINT NOT NULL DEFAULT 0, \n" +
                "  `is_dirty` TINYINT NOT NULL DEFAULT 0, \n" +
                "  `is_error` TINYINT NOT NULL DEFAULT 0, \n" +
                "  `error_message` CLOB NOT NULL DEFAULT '', \n" +
                "  `fetchTime` BIGINT NOT NULL DEFAULT 0, \n" +
                "  `URL` VARCHAR(1000) NOT NULL DEFAULT '' \n" +
                ");"
        try {
            val createTableStatement = conn.createStatement()
            logger.debug("Creating new table: $sql")
            createTableStatement.execute(sql)
            logger.debug("Migrating data to new database")
            moveFromOldDatabase()
        } catch (e: SQLException) {
            logger.error("Error executing migration from $migrateVersionFrom to $migrateVersionTo", e)
        }
    }

    private fun moveFromOldDatabase() {
        if (!oldDatabase.exist()) {
            logger.info("No old database")
            return
        }
        logger.info("Migrating old database")
        val oldConn = oldDatabase.connect()
        val newConn = newDatabase.connect()
        try {
            moveLocalIssues(oldConn, newConn)
        } catch (e: SQLException) {
            logger.warn("Error moving old database", e)
        } finally {
            oldConn.close()
            newConn.close()
        }
    }

    private fun moveLocalIssues(
            oldConn: Connection,
            newConn: Connection
    ) {
        val sql = "SELECT * FROM LocalIssue"
        val oldTickets: MutableList<TicketOld> = mutableListOf()
        try {
            val statement = oldConn.createStatement()
            val rs = statement.executeQuery(sql)
            while (rs.next()) {
                val localId = rs.getLong("_id")
                val key = rs.getString("key")
                val description = rs.getString("description")
                val remoteId = rs.getLong("id")
                val uri = rs.getString("uri")
                val deleted = rs.getInt("deleted")
                val dirty = rs.getInt("dirty")
                val error = rs.getInt("error")
                val errorMessage: String = rs.getString("errorMessage") ?: ""
                val oldTicket = TicketOld(
                        localId = localId,
                        key = key,
                        description = description,
                        remoteId = remoteId,
                        uri = uri,
                        deleted = deleted.toBoolean(),
                        dirty = dirty.toBoolean(),
                        error = error.toBoolean(),
                        errorMessage = errorMessage
                )
                oldTickets.add(oldTicket)
            }
        } catch (e: SQLException) {
            logger.warn("Could not read old database", e)
        }
        oldTickets
                .map {
                    val ticketCode = TicketCode.new(it.key)
                    TicketNew(
                            id = it.localId,
                            codeProject = ticketCode.codeProject,
                            codeNumber = ticketCode.codeNumber,
                            code = ticketCode.code,
                            description = it.description,
                            parentId = -1,
                            remoteId = it.remoteId,
                            isDeleted = it.deleted,
                            isDirty = it.dirty,
                            isError = it.error,
                            errorMessage = it.errorMessageSanitized,
                            fetchTime = 0,
                            url = it.uri
                    )
                }.forEach { newTicket ->
                    try {
                        val preparedStatement = newConn.prepareStatement(
                                "INSERT INTO ticket(" +
                                        "code_project," +
                                        "code_number," +
                                        "code," +
                                        "description," +
                                        "parent_id," +
                                        "remote_id," +
                                        "is_deleted," +
                                        "is_dirty," +
                                        "is_error," +
                                        "error_message," +
                                        "fetchTime," +
                                        "URL" +
                                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                        )
                        preparedStatement.setString(1, newTicket.codeProject)
                        preparedStatement.setString(2, newTicket.codeNumber)
                        preparedStatement.setString(3, newTicket.code)
                        preparedStatement.setString(4, newTicket.description)
                        preparedStatement.setLong(5, newTicket.parentId)
                        preparedStatement.setLong(6, newTicket.remoteId)
                        preparedStatement.setBoolean(7, newTicket.isDeleted)
                        preparedStatement.setBoolean(8, newTicket.isDirty)
                        preparedStatement.setBoolean(9, newTicket.isError)
                        preparedStatement.setString(10, newTicket.errorMessage)
                        preparedStatement.setLong(11, newTicket.fetchTime)
                        preparedStatement.setString(12, newTicket.url)
                        preparedStatement.execute()
                    } catch (e: SQLException) {
                        logger.warn("Error inserting value $newTicket due to ${e.message}")
                    }
                }
    }

    private data class TicketOld(
            val localId: Long,
            val key: String,
            val description: String,
            val remoteId: Long,
            val uri: String,
            val deleted: Boolean,
            val dirty: Boolean,
            val error: Boolean,
            val errorMessage: String
    ) {
        val descriptionSanitized = description
                .replace("\"", "'")
        val errorMessageSanitized = if (errorMessage.contains("null")) {
            ""
        } else {
            errorMessage
        }
    }

    private data class TicketNew(
            val id: Long,
            val codeProject: String,
            val codeNumber: String,
            val code: String,
            val description: String,
            val parentId: Long,
            val remoteId: Long,
            val isDeleted: Boolean,
            val isDirty: Boolean,
            val isError: Boolean,
            val errorMessage: String,
            val fetchTime: Long,
            val url: String
    )

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}