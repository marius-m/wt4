package lt.markmerkk

import lt.markmerkk.entities.*
import lt.markmerkk.schema1.Tables.WORKLOG
import lt.markmerkk.schema1.tables.records.WorklogRecord
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.jooq.DSLContext
import org.jooq.Result
import org.slf4j.LoggerFactory
import rx.Single

class WorklogRepository(
        private val connProvider: DBConnProvider,
        private val timeProvider: TimeProvider
) {

    fun loadWorklogs(
            from: LocalDate,
            to: LocalDate
    ): Single<List<Log>> {
        val localTime = LocalTime.MIDNIGHT
        return Single.defer {
            val fromAsMillis = timeProvider.roundMillis(from.toDateTime(localTime))
            val toAsMillis = timeProvider.roundMillis(to.toDateTime(localTime))
            val dbResult: Result<WorklogRecord> = connProvider.dsl
                    .select()
                    .from(WORKLOG)
                    .where(
                            WORKLOG.START.greaterOrEqual(fromAsMillis)
                                    .and(WORKLOG.END.lessOrEqual(toAsMillis))
                    )
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

    fun loadWorklogsSync(from: LocalDate, to: LocalDate): List<Log> {
        return loadWorklogs(from, to).toBlocking().value()
    }

    fun insertOrUpdate(log: Log): Single<Int> {
        return Single.defer {
            val worklogExist = isWorklogExistLocally(connProvider.dsl, log)
            val remoteData: RemoteData = log.remoteData ?: RemoteData.asEmpty()
            val result = if (worklogExist) {
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
            } else {
                connProvider.dsl.insertInto(
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
                ).execute()
            }
            Single.just(result)
        }
    }

    fun insertOrUpdateSync(log: Log): Int {
        return insertOrUpdate(log).toBlocking().value()
    }

    fun update(log: Log): Single<Int> {
        return Single.defer {
            val worklogExist = isWorklogExistLocally(connProvider.dsl, log)
            val remoteData: RemoteData = log.remoteData ?: RemoteData.asEmpty()
            val result = if (worklogExist) {
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

            } else {
                Const.NO_ID.toInt()
            }
            Single.just(result)
        }
    }

    fun reinsert(oldEntryLocalId: Long, newEntry: Log): Single<Int> {
        return delete(oldEntryLocalId)
                .flatMap { insertOrUpdate(newEntry) }
    }

    fun reinsertSync(oldEntryLocalId: Long, newEntry: Log): Int {
        return reinsert(oldEntryLocalId, newEntry).toBlocking().value()
    }

    fun updateSync(log: Log): Int {
        return update(log).toBlocking().value()
    }

    fun findById(localId: Long): Log? {
        if (localId <= 0) {
            return null
        }
        val dbResult: Result<WorklogRecord> = connProvider.dsl
                .select()
                .from(WORKLOG)
                .where(WORKLOG.ID.eq(localId.toInt()))
                .fetchInto(WORKLOG)
        return dbResult
                .map { worklog ->
                    Log.fromDatabase(
                            timeProvider = timeProvider,
                            id = worklog.id.toLong(),
                            start = worklog.start,
                            end = worklog.end,
                            code = worklog.code,
                            comment = worklog.comment,
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

    fun findByRemoteId(remoteId: Long): Log? {
        if (remoteId <= 0) {
            return null
        }
        val dbResult: Result<WorklogRecord> = connProvider.dsl
                .select()
                .from(WORKLOG)
                .where(WORKLOG.REMOTE_ID.eq(remoteId))
                .fetchInto(WORKLOG)
        return dbResult
                .map { worklog ->
                    Log.fromDatabase(
                            timeProvider = timeProvider,
                            id = worklog.id.toLong(),
                            start = worklog.start,
                            end = worklog.end,
                            code = worklog.code,
                            comment = worklog.comment,
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

    fun delete(localId: Long): Single<Int> {
        return Single.defer {
            val result = connProvider.dsl.delete(WORKLOG)
                    .where(WORKLOG.ID.eq(localId.toInt()))
                    .execute()
            Single.just(result)
        }
    }

    fun deleteSync(localId: Long): Int {
        return delete(localId).toBlocking().value()
    }

    private fun isWorklogExistLocally(dslContext: DSLContext, log: Log): Boolean {
        val localId = log.id
        if (localId == Const.NO_ID) {
            return false
        }
        val logCount = dslContext.selectCount()
                .from(WORKLOG)
                .where(WORKLOG.ID.eq(localId.toInt()))
                .fetchOne(0, Integer::class.java)
        return logCount > 0
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}