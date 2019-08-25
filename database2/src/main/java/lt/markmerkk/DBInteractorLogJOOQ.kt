package lt.markmerkk

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.schema1.Tables.WORKLOG
import lt.markmerkk.schema1.tables.records.WorklogRecord
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.slf4j.LoggerFactory

/**
 * Responsible for doing raw calls to database
 */
class DBInteractorLogJOOQ(
        private val connProvider: DBConnProvider,
        private val timeProvider: TimeProvider
) {

    /**
     * Loads worklog entries by date range
     */
    fun loadWorklogs(
            from: LocalDate,
            to: LocalDate
    ): List<Log> {
        val localTime = LocalTime.MIDNIGHT
        val fromAsMillis = timeProvider.roundMillis(from.toDateTime(localTime))
        val toAsMillis = timeProvider.roundMillis(to.toDateTime(localTime))
        val dbResult: org.jooq.Result<WorklogRecord> = connProvider.dsl
                .select()
                .from(WORKLOG)
                .where(
                        WORKLOG.START.greaterOrEqual(fromAsMillis)
                                .and(WORKLOG.START.lessOrEqual(toAsMillis))
                )
                .fetchInto(WORKLOG)
        val worklogs: List<Log> = dbResult
                .map { worklog ->
                    Log.createFromDatabase(
                            timeProvider = timeProvider,
                            id = worklog.id.toLong(),
                            start = worklog.start,
                            end = worklog.end,
                            code = worklog.code,
                            comment = worklog.comment,
                            systemNote = worklog.systemNote,
                            remoteData = RemoteData.new(
                                    isDeleted = worklog.isDeleted.toBoolean(),
                                    isDirty = worklog.isDirty.toBoolean(),
                                    isError = worklog.isError.toBoolean(),
                                    errorMessage = worklog.errorMessage,
                                    fetchTime = worklog.fetchtime,
                                    url = worklog.url
                            )
                    )
                }
        return worklogs
    }

    /**
     * Inserts entry by it's [Log.id]
     */
    fun insert(log: Log): Int {
        val remoteData: RemoteData = log.remoteData ?: RemoteData.asEmpty()
        val resultInsert: Int? = connProvider.dsl.insertInto(
                WORKLOG,
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
        ).values(
                log.time.startAsRaw,
                log.time.endAsRaw,
                log.time.durationAsRaw,
                log.code.code,
                log.comment,
                remoteData.remoteId,
                remoteData.isDeleted.toByte(),
                remoteData.isDirty.toByte(),
                remoteData.isError.toByte(),
                remoteData.errorMessage,
                remoteData.fetchTime,
                remoteData.url
        ).returning(WORKLOG.ID)
                .fetchOne()
                ?.getValue(WORKLOG.ID)
        return resultInsert ?: Const.NO_ID.toInt()
    }

    /**
     * Updates log entry where [Log.id]
     */
    fun update(log: Log): Int {
        val remoteData: RemoteData = log.remoteData ?: RemoteData.asEmpty()
        connProvider.dsl.update(WORKLOG)
                .set(WORKLOG.START, log.time.startAsRaw)
                .set(WORKLOG.END, log.time.endAsRaw)
                .set(WORKLOG.DURATION, log.time.durationAsRaw)
                .set(WORKLOG.CODE, log.code.code)
                .set(WORKLOG.COMMENT, log.comment)
                .set(WORKLOG.REMOTE_ID, remoteData.remoteId)
                .set(WORKLOG.IS_DELETED, remoteData.isDeleted.toByte())
                .set(WORKLOG.IS_DIRTY, remoteData.isDirty.toByte())
                .set(WORKLOG.IS_ERROR, remoteData.isError.toByte())
                .set(WORKLOG.ERROR_MESSAGE, remoteData.errorMessage)
                .set(WORKLOG.FETCHTIME, remoteData.fetchTime)
                .set(WORKLOG.URL, remoteData.url)
                .where(WORKLOG.ID.eq(log.id.toInt()))
                .execute()
        return log.id.toInt()
    }

    /**
     * Deletes a log entry by it's [Log.id]
     */
    fun deleteByLocalId(localId: Long): Int {
        val resultDelete: Int? = connProvider.dsl.delete(WORKLOG)
                .where(WORKLOG.ID.eq(localId.toInt()))
                .returning(WORKLOG.ID)
                .fetchOne()
                ?.getValue(WORKLOG.ID)
        return resultDelete ?: Const.NO_ID.toInt()
    }

    /**
     * Deletes a log entry by it's [Log.remoteData.removeId]
     */
    fun deleteByRemoteId(remoteId: Long): Int {
        val resultDelete: Int? = connProvider.dsl.delete(WORKLOG)
                .where(WORKLOG.REMOTE_ID.eq(remoteId))
                .returning(WORKLOG.ID)
                .fetchOne()
                ?.getValue(WORKLOG.ID)
        return resultDelete ?: Const.NO_ID.toInt()
    }

    /**
     * Finds a [Log] entry by its local [Log.id]
     */
    fun findByLocalId(localId: Long): Log? {
        if (localId <= 0) {
            return null
        }
        val dbResult: org.jooq.Result<WorklogRecord> = connProvider.dsl
                .select()
                .from(WORKLOG)
                .where(WORKLOG.ID.eq(localId.toInt()))
                .fetchInto(WORKLOG)
        return dbResult
                .map { worklog ->
                    Log.createFromDatabase(
                            timeProvider = timeProvider,
                            id = worklog.id.toLong(),
                            start = worklog.start,
                            end = worklog.end,
                            code = worklog.code,
                            comment = worklog.comment,
                            systemNote = worklog.systemNote,
                            remoteData = RemoteData.new(
                                    isDeleted = worklog.isDeleted.toBoolean(),
                                    isDirty = worklog.isDirty.toBoolean(),
                                    isError = worklog.isError.toBoolean(),
                                    errorMessage = worklog.errorMessage,
                                    fetchTime = worklog.fetchtime,
                                    url = worklog.url
                            )
                    )
                }.firstOrNull()
    }

    /**
     * Find a log entry by it's [Log.remoteData.remoteId]
     */
    fun findByRemoteId(remoteId: Long): Log? {
        if (remoteId <= 0) {
            return null
        }
        val dbResult: org.jooq.Result<WorklogRecord> = connProvider.dsl
                .select()
                .from(WORKLOG)
                .where(WORKLOG.REMOTE_ID.eq(remoteId))
                .fetchInto(WORKLOG)
        return dbResult
                .map { worklog ->
                    Log.createFromDatabase(
                            timeProvider = timeProvider,
                            id = worklog.id.toLong(),
                            start = worklog.start,
                            end = worklog.end,
                            code = worklog.code,
                            comment = worklog.comment,
                            systemNote = worklog.systemNote,
                            remoteData = RemoteData.new(
                                    isDeleted = worklog.isDeleted.toBoolean(),
                                    isDirty = worklog.isDirty.toBoolean(),
                                    isError = worklog.isError.toBoolean(),
                                    errorMessage = worklog.errorMessage,
                                    fetchTime = worklog.fetchtime,
                                    url = worklog.url
                            )
                    )
                }.firstOrNull()
    }

    /**
     * Checks if an entry exist with it's [Log.id]
     */
    fun existAsLocal(localId: Long): Boolean {
        if (localId == Const.NO_ID) {
            return false
        }
        val logCount = connProvider.dsl.selectCount()
                .from(WORKLOG)
                .where(WORKLOG.ID.eq(localId.toInt()))
                .fetchOne(0, Integer::class.java)
        return logCount > 0
    }

    /**
     * Checks if an entry exist with it's [Log.remoteData.remoteId]
     */
    fun existAsRemote(remoteId: Long): Boolean {
        if (remoteId == Const.NO_ID) {
            return false
        }
        val logCount = connProvider.dsl.selectCount()
                .from(WORKLOG)
                .where(WORKLOG.REMOTE_ID.eq(remoteId))
                .fetchOne(0, Integer::class.java)
        return logCount > 0
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}