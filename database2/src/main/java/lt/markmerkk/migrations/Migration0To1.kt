package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.schema1.Tables.LOG
import lt.markmerkk.schema1.Tables.TICKET
import lt.markmerkk.toBoolean
import lt.markmerkk.toByte
import lt.markmerkk.toInt
import org.joda.time.DateTime
import org.jooq.DSLContext
import org.jooq.InsertValuesStep13
import org.jooq.SQLDialect
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.sql.Connection

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
                .column(TICKET.CREATETIME)
                .column(TICKET.UPDATETIME)
                .column(TICKET.URL)
                .execute()
        currentDsl.createTableIfNotExists(LOG)
                .column(LOG.ID)
                .column(LOG.START)
                .column(LOG.END)
                .column(LOG.DURATION)
                .column(LOG.CODE)
                .column(LOG.COMMENT)
                .column(LOG.REMOTE_ID)
                .column(LOG.IS_DELETED)
                .column(LOG.IS_DIRTY)
                .column(LOG.IS_ERROR)
                .column(LOG.ERROR_MESSAGE)
                .column(LOG.FETCHTIME)
                .column(LOG.CREATETIME)
                .column(LOG.UPDATETIME)
                .column(LOG.URL)
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
            moveLocalIssues(oldDsl, currentDsl, now)
        } catch (e: DataAccessException) {
            logger.warn("Error moving old database", e)
        } finally {
            oldConn.close()
        }
    }

    private fun moveLocalIssues(
            oldDsl: DSLContext,
            currentDsl: DSLContext,
            now: Long
    ) {
        val localIssues = oldDsl.fetch("SELECT * FROM LocalIssue")
        val oldIssueInsertList = localIssues.map {
            val project = it.getValue("project") as String
            val key = it.getValue("key") as String
            val description = it.getValue("description") as String
            val createDate = it.getValue("createDate") as Int
            val updateDate = it.getValue("updateDate") as Int
            val id = it.getValue("id") as Int
            val _id = it.getValue("_id") as Int
            val uri = it.getValue("uri") as String
            val deleted = it.getValue("deleted") as Int
            val dirty = it.getValue("dirty") as Int
            val error = it.getValue("error") as Int
            val errorMessage = it.getValue("errorMessage") as String
            val downloadMillis = it.getValue("download_millis") as Int
            Ticket.new(
                    code = key,
                    description = description,
                    remoteData = RemoteData.new(
                            remoteId = _id.toLong(),
                            isDeleted = deleted.toBoolean(),
                            isDirty = dirty.toBoolean(),
                            isError = error.toBoolean(),
                            errorMessage = errorMessage,
                            createTime = now,
                            updateTime = now,
                            uri = uri,
                            fetchTime = now
                    )
            )
        }.map { ticket ->
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
                            TICKET.CREATETIME,
                            TICKET.UPDATETIME,
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
                            remoteData.createTime,
                            remoteData.updateTime,
                            remoteData.url
                    )
        }
        currentDsl.batch(oldIssueInsertList)
                .execute()
    }

    companion object {
        private val logger = LoggerFactory.getLogger("DB")!!
    }
}