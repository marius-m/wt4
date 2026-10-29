package lt.markmerkk

import lt.markmerkk.db.WT4Database
import lt.markmerkk.db.Worklog
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.slf4j.LoggerFactory

/**
 * Responsible for doing raw calls to database
 */
class DBInteractorLog(
        private val database: WT4Database,
        private val timeProvider: TimeProvider
) {

    private val queries = database.worklogQueries

    /**
     * Loads worklog entries by date range
     */
    fun loadWorklogs(
            from: LocalDate,
            to: LocalDate
    ): List<Log> {
        val localTime = LocalTime.MIDNIGHT
        val fromAsMillis = from.toDateTime(localTime).roundMillis()
        val toAsMillis = to.toDateTime(localTime).roundMillis()
        return queries.selectByStartRange(fromMillis = fromAsMillis, toMillis = toAsMillis)
                .executeAsList()
                .map { it.toLog() }
    }

    /**
     * Inserts entry by it's [Log.id]
     * @return id of the newly inserted entry
     */
    fun insert(log: Log): Long {
        val remoteData: RemoteData = log.remoteData ?: RemoteData.asEmpty()
        return database.transactionWithResult {
            queries.insert(
                    start = log.time.startAsRaw,
                    end = log.time.endAsRaw,
                    duration = log.time.durationAsRaw,
                    code = log.code.code,
                    comment = log.comment,
                    system_note = log.systemNote,
                    author = log.author,
                    remote_id = remoteData.remoteId,
                    is_deleted = remoteData.isDeleted,
                    is_dirty = remoteData.isDirty,
                    is_error = remoteData.isError,
                    error_message = remoteData.errorMessage,
                    fetchTime = remoteData.fetchTime,
                    URL = remoteData.url
            )
            queries.lastInsertRowId().executeAsOne()
        }
    }

    /**
     * Updates log entry where [Log.id]
     */
    fun update(log: Log): Long {
        val remoteData: RemoteData = log.remoteData ?: RemoteData.asEmpty()
        queries.update(
                start = log.time.startAsRaw,
                end = log.time.endAsRaw,
                duration = log.time.durationAsRaw,
                code = log.code.code,
                comment = log.comment,
                system_note = log.systemNote,
                author = log.author,
                remote_id = remoteData.remoteId,
                is_deleted = remoteData.isDeleted,
                is_dirty = remoteData.isDirty,
                is_error = remoteData.isError,
                error_message = remoteData.errorMessage,
                fetchTime = remoteData.fetchTime,
                URL = remoteData.url,
                id = log.id
        )
        return log.id
    }

    /**
     * Deletes a log entry by it's [Log.id]
     * @return id of deleted entry or [Const.NO_ID] if nothing was deleted
     */
    fun deleteByLocalId(localId: Long): Long {
        return database.transactionWithResult {
            if (existAsLocal(localId)) {
                queries.deleteById(localId)
                localId
            } else {
                Const.NO_ID
            }
        }
    }

    /**
     * Deletes a log entry by it's [Log.remoteData.removeId]
     * @return local id of deleted entry or [Const.NO_ID] if nothing was deleted
     */
    fun deleteByRemoteId(remoteId: Long): Long {
        return database.transactionWithResult {
            val localId = queries.selectByRemoteId(remoteId)
                    .executeAsList()
                    .firstOrNull()
                    ?.id ?: Const.NO_ID
            queries.deleteByRemoteId(remoteId)
            localId
        }
    }

    /**
     * Finds a [Log] entry by its local [Log.id]
     */
    fun findByLocalId(localId: Long): Log? {
        if (localId <= 0) {
            return null
        }
        return queries.selectById(localId)
                .executeAsList()
                .firstOrNull()
                ?.toLog()
    }

    /**
     * Find a log entry by it's [Log.remoteData.remoteId]
     */
    fun findByRemoteId(remoteId: Long): Log? {
        if (remoteId <= 0) {
            return null
        }
        return queries.selectByRemoteId(remoteId)
                .executeAsList()
                .firstOrNull()
                ?.toLog()
    }

    /**
     * Checks if an entry exist with it's [Log.id]
     */
    fun existAsLocal(localId: Long): Boolean {
        if (localId == Const.NO_ID) {
            return false
        }
        return queries.countById(localId).executeAsOne() > 0
    }

    /**
     * Checks if an entry exist with it's [Log.remoteData.remoteId]
     */
    fun existAsRemote(remoteId: Long): Boolean {
        if (remoteId == Const.NO_ID) {
            return false
        }
        return queries.countByRemoteId(remoteId).executeAsOne() > 0
    }

    private fun Worklog.toLog(): Log {
        return Log.createFromDatabase(
                timeProvider = timeProvider,
                id = id,
                start = start,
                end = end,
                code = code,
                comment = comment,
                systemNote = system_note,
                author = author,
                remoteData = RemoteData.new(
                        isDeleted = is_deleted,
                        isDirty = is_dirty,
                        isError = is_error,
                        errorMessage = error_message,
                        fetchTime = fetchTime,
                        url = URL
                )
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}
