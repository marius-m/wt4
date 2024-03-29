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

    fun insertOrUpdateSync(log: Log): Long {
        val insertRemoteLog = log.isRemote
        val insertLocalLog = !log.isRemote
        val localId = log.id
        val remoteId = log.remoteData?.remoteId ?: Const.NO_ID
        val existAsLocal = dbInteractor.existAsLocal(localId = localId)
        val existAsRemote = dbInteractor.existAsRemote(remoteId = remoteId)

        // insert local log
        when {
            insertLocalLog -> {
                if (existAsLocal) {
                    return dbInteractor.update(log)
                }
                dbInteractor.insert(log)
            }
            insertRemoteLog -> {
                if (!existAsLocal && !existAsRemote) {
                    return dbInteractor.insert(log)
                }
                dbInteractor.deleteByLocalId(localId)
                dbInteractor.deleteByRemoteId(remoteId)
                return dbInteractor.insert(
                        Log.new(
                                timeProvider,
                                start = log.time.startAsRaw,
                                end = log.time.endAsRaw,
                                code = log.code.code,
                                comment = log.comment,
                                systemNote = "",
                                author = log.author,
                                remoteData = log.remoteData
                        )
                )
            }
        }
        return Const.NO_ID
    }

    fun insertOrUpdate(log: Log): Single<Long> {
        return Single.defer {
            Single.just(insertOrUpdateSync(log))
        }
    }

    // todo fix this as this needs tinkering, as whenever update log, remote logs should be marked for deletion
    fun updateSync(log: Log): Long {
        val updateRemoteLog = log.isRemote
        val updateLocalLog = !log.isRemote
        val localId = log.id
        val remoteId = log.remoteData?.remoteId ?: Const.NO_ID
        val existAsLocal = dbInteractor.existAsLocal(localId = localId)
        val existAsRemote = dbInteractor.existAsRemote(remoteId = remoteId)

        if (!existAsLocal && !existAsRemote) {
            return Const.NO_ID
        }

        // insert local log
        when {
            updateLocalLog -> {
                if (existAsLocal && !existAsRemote) {
                    return dbInteractor.update(log)
                }
            }
            updateRemoteLog -> {
                if (existAsLocal && !existAsRemote) {
                    dbInteractor.deleteByLocalId(localId)
                    dbInteractor.deleteByRemoteId(remoteId)
                }
                if (existAsRemote) {
                    dbInteractor.update(log.markAsDeleted())
                }
                return dbInteractor.insert(
                        Log.new(
                                timeProvider,
                                start = log.time.startAsRaw,
                                end = log.time.endAsRaw,
                                code = log.code.code,
                                comment = log.comment,
                                systemNote = "",
                                author = log.author,
                                remoteData = null
                        )
                )
            }
        }
        return Const.NO_ID
    }

    fun update(log: Log): Single<Long> {
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

    fun deleteSync(log: Log): Long {
        val localId = log.id
        val remoteId = log.remoteData?.remoteId ?: Const.NO_ID
        val existAsLocal = dbInteractor.existAsLocal(localId = localId)
        val existAsRemote = dbInteractor.existAsRemote(remoteId = remoteId)
        if (existAsLocal && !existAsRemote) {
            return dbInteractor.deleteByLocalId(log.id)
        }
        if (existAsRemote) {
            return dbInteractor.update(log.markAsDeleted())
        }
        return Const.NO_ID
    }

    fun delete(log: Log): Single<Long> {
        return Single.defer {
            Single.just(deleteSync(log))
        }
    }

    fun hardDeleteRemoteSync(remoteId: Long): Long {
        return dbInteractor.deleteByRemoteId(remoteId)
    }

    fun hardDeleteRemote(remoteId: Long): Single<Long> {
        return Single.defer {
            Single.just(hardDeleteRemoteSync(remoteId))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}