package lt.markmerkk

import lt.markmerkk.entities.Log
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Single

class WorklogStorage(
        private val timeProvider: TimeProvider,
        private val dbInteractor: DBInteractorLogJOOQ
) {

    fun loadWorklogsSync(from: LocalDate, to: LocalDate): List<Log> {
        return dbInteractor.loadWorklogs(from, to)
    }

    fun loadWorklogs(
            from: LocalDate,
            to: LocalDate
    ): Single<List<Log>> {
        return Single.defer {
            Single.just(loadWorklogsSync(from, to))
        }
    }

    fun insertOrUpdateSync(log: Log): Int {
        val insertingRemoteLog = log.isRemote
        val existAsLocal = dbInteractor.isWorklogExistLocally(localId = log.id)
        val existAsRemote = dbInteractor.isWorklogExistRemotely(remoteId = log.remoteData?.remoteId ?: Const.NO_ID)
        if (!insertingRemoteLog) { // insert local log
            if (existAsLocal) {
                return dbInteractor.update(log)
            }
            dbInteractor.insert(log)
        } else { // insert remote log
            if (existAsLocal || existAsRemote) {
                return dbInteractor.update(log)
            }
            return dbInteractor.insert(log)
        }
        return Const.NO_ID.toInt()
    }

    fun insertOrUpdate(log: Log): Single<Int> {
        return Single.defer {
            Single.just(insertOrUpdateSync(log))
        }
    }

    fun updateSync(log: Log): Int {
        val worklogExist = dbInteractor.isWorklogExistLocally(log.id)
        return if (worklogExist) {
            dbInteractor.update(log)
        } else {
            Const.NO_ID.toInt()
        }
    }

    fun update(log: Log): Single<Int> {
        return Single.defer {
            Single.just(updateSync(log))
        }
    }

    fun findById(localId: Long): Log? {
        if (localId <= 0) {
            return null
        }
        return dbInteractor.findByLocalId(localId)
    }

    fun findByRemoteId(remoteId: Long): Log? {
        if (remoteId <= 0) {
            return null
        }
        return dbInteractor.findByRemoteId(remoteId)
    }

    fun deleteSync(localId: Long): Int {
        return dbInteractor.delete(localId)
    }

    fun delete(localId: Long): Single<Int> {
        return Single.defer {
            Single.just(deleteSync(localId))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}