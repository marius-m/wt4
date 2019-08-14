package lt.markmerkk.migrations

import lt.markmerkk.*
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Log
import lt.markmerkk.schema1.tables.Worklog.WORKLOG
import org.joda.time.DateTime
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * Migration for worklog (not used yet)
 */
class Migration1To2(
        private val oldDatabase: DBConnProvider,
        private val timeProvider: TimeProvider
): DBMigration {

    override val migrateVersionFrom: Int = 1
    override val migrateVersionTo: Int = 2

    override fun migrate(conn: Connection) {
        val currentDsl = DSL.using(conn, SQLDialect.SQLITE)
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
            moveSingleLog(oldConn, currentDsl, now)
        } catch (e: DataAccessException) {
            logger.warn("Error moving old database", e)
        } finally {
            oldConn.close()
        }
    }

    private fun moveSingleLog(
            oldConnection: Connection,
            currentDsl: DSLContext,
            now: Long
    ) {
        val sql = "SELECT * FROM Log"
        val oldWorklogs = mutableListOf<Log>()
        try {
            val statement = oldConnection.createStatement()
            val rs = statement.executeQuery(sql)
            while (rs.next()) {
                val start = rs.getLong("start")
                val end = rs.getLong("end")
                val task = rs.getString("task")
                val comment = rs.getString("comment")
                val _id = rs.getLong("_id")
                val uri = rs.getString("uri")
                val deleted = rs.getInt("deleted")
                val dirty = rs.getInt("dirty")
                val error = rs.getInt("error")
                val errorMessage: String = rs.getString("errorMessage") ?: ""
                val worklog = Log.newRaw(
                        timeProvider = timeProvider,
                        start = start,
                        end = end,
                        code = task,
                        comment = comment,
                        remoteData = RemoteData.new(
                                remoteId = _id,
                                isDeleted = deleted.toBoolean(),
                                isDirty = dirty.toBoolean(),
                                isError = error.toBoolean(),
                                errorMessage = errorMessage,
                                url = uri,
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
                            timeProvider.roundMillis(worklog.time.start),
                            timeProvider.roundMillis(worklog.time.end),
                            worklog.time.duration.millis,
                            worklog.code.code,
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
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}