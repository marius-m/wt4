package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.LogTime
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.toBoolean
import lt.markmerkk.utils.UriUtils
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * Migration for worklog (not used yet)
 */
class Migration1To2(
        private val oldDatabase: DBConnProvider,
        private val newDatabase: DBConnProvider,
        private val timeProvider: TimeProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 1
    override val migrateVersionTo: Int = 2

    override fun migrate(conn: Connection) {
        val sql = "" +
                "CREATE TABLE `worklog` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `start` BIGINT NOT NULL DEFAULT 0,\n" +
                "  `end` BIGINT NOT NULL DEFAULT 0,\n" +
                "  `duration` BIGINT NOT NULL DEFAULT 0,\n" +
                "  `code` VARCHAR(50) DEFAULT '' NOT NULL,\n" +
                "  `comment` TEXT DEFAULT '' NOT NULL,\n" +
                "  `remote_id` BIGINT NOT NULL DEFAULT -1,\n" +
                "  `is_deleted` TINYINT NOT NULL DEFAULT 0,\n" +
                "  `is_dirty` TINYINT NOT NULL DEFAULT 0,\n" +
                "  `is_error` TINYINT NOT NULL DEFAULT 0,\n" +
                "  `error_message` TEXT NOT NULL DEFAULT '',\n" +
                "  `fetchTime` BIGINT NOT NULL DEFAULT 0,\n" +
                "  `URL` VARCHAR(1000) NOT NULL DEFAULT ''\n" +
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
        val now = DateTime.now().millis
        if (!oldDatabase.exist()) {
            logger.info("No old database")
            return
        }
        logger.info("Migrating old database")
        val oldConn = oldDatabase.connect()
        val newConn = newDatabase.connect()
        try {
            moveSingleLog(oldConn, newConn)
        } catch (e: SQLException) {
            logger.warn("Error moving old database", e)
        } finally {
            oldConn.close()
            newConn.close()
        }
    }

    private fun moveSingleLog(
            oldConnection: Connection,
            newConnection: Connection
    ) {
        val sql = "SELECT * FROM Log"
        val oldWorklogs: MutableList<LocalLogOld> = mutableListOf()
        try {
            val statement = oldConnection.createStatement()
            val rs = statement.executeQuery(sql)
            while (rs.next()) {
                val start = rs.getLong("start")
                val end = rs.getLong("end")
                val task = rs.getString("task")
                val comment = rs.getString("comment")
                val uri = rs.getString("uri")
                val deleted = rs.getInt("deleted")
                val dirty = rs.getInt("dirty")
                val error = rs.getInt("error")
                val errorMessage: String = rs.getString("errorMessage") ?: ""
                oldWorklogs.add(LocalLogOld(
                        start = start,
                        end = end,
                        taskRaw = task,
                        commentRaw = comment,
                        uriRaw = uri,
                        deleted = deleted.toBoolean(),
                        dirty = dirty.toBoolean(),
                        error = error.toBoolean(),
                        errorMessageRaw = errorMessage
                ))
            }
        } catch (e: SQLException) {
            logger.warn("Could not read old database", e)
        }
        oldWorklogs.map {
            val logTime = LogTime.fromRaw(
                    timeProvider,
                    it.start,
                    it.end
            )
            val ticketCode = TicketCode.new(it.task)
            val remoteId = UriUtils.parseUri(it.uri)
            LocalLogNew(
                    start = logTime.startAsRaw,
                    end = logTime.endAsRaw,
                    duration = logTime.durationAsRaw,
                    code = ticketCode.code,
                    comment = it.comment,
                    remoteId = remoteId,
                    isDeleted = it.deleted,
                    isError = it.error,
                    isDirty = it.dirty,
                    errorMessage = it.errorMessage,
                    fetchTime = 0,
                    url = it.uri
            )
        }.forEach { newLog ->
            try {
                val preparedStatement = newConnection.prepareStatement(
                        "INSERT INTO worklog(" +
                                "start," +
                                "end," +
                                "duration," +
                                "code," +
                                "comment," +
                                "remote_id," +
                                "is_deleted," +
                                "is_dirty," +
                                "is_error," +
                                "error_message," +
                                "fetchTime," +
                                "URL" +
                                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                )
                preparedStatement.setLong(1, newLog.start)
                preparedStatement.setLong(2, newLog.end)
                preparedStatement.setLong(3, newLog.duration)
                preparedStatement.setString(4, newLog.code)
                preparedStatement.setString(5, newLog.comment)
                preparedStatement.setLong(6, newLog.remoteId)
                preparedStatement.setBoolean(7, newLog.isDeleted)
                preparedStatement.setBoolean(8, newLog.isDirty)
                preparedStatement.setBoolean(9, newLog.isError)
                preparedStatement.setString(10, newLog.errorMessage)
                preparedStatement.setLong(11, newLog.fetchTime)
                preparedStatement.setString(12, newLog.url)
                preparedStatement.execute()
            } catch (e: SQLException) {
                logger.warn("Error inserting value $newLog due to ${e.message}")
            }
        }
    }

    private data class LocalLogOld(
            val start: Long,
            val end: Long,
            val taskRaw: String?,
            val commentRaw: String?,
            val uriRaw: String?,
            val deleted: Boolean,
            val dirty: Boolean,
            val error: Boolean,
            val errorMessageRaw: String?
    ) {
        val task = taskRaw?.replace("null", "") ?: ""
        val comment = commentRaw?.replace("null", "") ?: ""
        val uri = uriRaw?.replace("null", "") ?: ""
        val errorMessage = errorMessageRaw?.replace("null", "") ?: ""
    }

    private data class LocalLogNew(
            val start: Long,
            val end: Long,
            val duration: Long,
            val code: String,
            val comment: String,
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