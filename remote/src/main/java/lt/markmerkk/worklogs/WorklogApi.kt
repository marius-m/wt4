package lt.markmerkk.worklogs

import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Completable
import rx.Observable
import rx.Single

class WorklogApi(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraWorklogInteractor: JiraWorklogInteractor,
        private val ticketStorage: TicketStorage,
        private val worklogStorage: WorklogStorage,
        private val userSettings: UserSettings
) {

    /**
     * Fetches and stores remote [Log] into database
     */
    fun fetchLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        val startFormat = LogFormatters.shortFormatDate.print(start)
        val endFormat = LogFormatters.shortFormatDate.print(end)
        val jql = "(worklogDate >= \"$startFormat\" && worklogDate <= \"$endFormat\" && worklogAuthor = currentUser())"
        return Completable.fromAction { logger.info("Starting to fetch new logs from $start to $end") }
                .andThen(
                        jiraWorklogInteractor.searchWorlogs(
                                fetchTime = fetchTime,
                                jql = jql,
                                startDate = start,
                                endDate = end
                        )
                )
                .onErrorResumeNext { error ->
                    logger.warn("Error fetching remote worklogs", error)
                    Observable.empty()
                }
                .map { (ticket, worklogs) ->
                    ticketStorage.insertOrUpdateSync(ticket)
                    worklogs.forEach {
                        worklogStorage.insertOrUpdateSync(it)
                    }
                }
                .toList()
                .toCompletable()
    }

    /**
     * Removes all unknown remote [Log]'s from database. 'Unknown' - exist in jira and does not exist in our
     * database as a remote log.
     */
    // todo should be optimized together when fetching logs the first time
    fun deleteUnknownLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        val startFormat = LogFormatters.shortFormatDate.print(start)
        val endFormat = LogFormatters.shortFormatDate.print(end)
        val jql = "(worklogDate >= \"$startFormat\" && worklogDate <= \"$endFormat\" && worklogAuthor = currentUser())"
        return Completable.fromAction { logger.info("Starting to remove unknown remote logs") }
                .andThen(
                        worklogStorage.loadWorklogs(start, end)
                                .zipWith(
                                        jiraWorklogInteractor.searchWorklogsAsList(fetchTime, jql, start, end),
                                        { logsDb, logsApi -> Pair(logsDb, logsApi) }
                                )
                )
                .map { (logsDb, logsApi) ->
                    val dbRemoteIds = logsDb
                            .filter { it.isRemote }
                            .map { it.remoteData!!.remoteId }
                    val apiRemoteIds = logsApi
                            .map { it.remoteData!!.remoteId }
                    dbRemoteIds
                            .subtract(apiRemoteIds)
                            .forEach {
                                logger.info("Deleting worklog with remote ID $it as it is not found on remote anymore")
                                worklogStorage.hardDeleteRemoteSync(it)
                            }
                }
                .toCompletable()
    }

    /**
     * Removes all [Log] when are marked for deletion [Log.remoteData.isDelete]
     */
    fun deleteMarkedLogs(start: LocalDate, end: LocalDate): Completable {
        return Completable.fromAction { logger.info("Starting to delete worklogs from $start to $end") }
                .andThen(worklogStorage.loadWorklogs(start, end))
                .flatMapObservable { Observable.from(it) }
                .filter { it.isMarkedForDeletion }
                .flatMapSingle { worklog ->
                    jiraWorklogInteractor.delete(worklog)
                            .onErrorResumeNext { error ->
                                logger.warn("Error trying to delete a worklog", error)
                                Single.just(worklog.remoteData?.remoteId ?: Const.NO_ID)
                            }
                }
                .flatMapSingle { worklogStorage.hardDeleteRemote(it) }
                .toList()
                .toCompletable()
    }

    /**
     * Uploads all local logs for the provided time gap
     */
    fun uploadLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        return Completable.fromAction { logger.info("Starting to upload worklogs") }
                .andThen(worklogStorage.loadWorklogs(start, end))
                .flatMapObservable { Observable.from(it) }
                .flatMapSingle { uploadLog(fetchTime, it) }
                .flatMap { uploadStatus ->
                    when (uploadStatus) {
                        is WorklogUploadSuccess -> {
                            logger.info("Success uploading worklog (${uploadStatus.remoteLog.id})")
                            worklogStorage
                                    .insertOrUpdate(uploadStatus.remoteLog)
                                    .toObservable()
                        }
                        is WorklogUploadError -> {
                            logger.info("Error uploading ${uploadStatus.error}")
                            Observable.empty()
                        }
                        is WorklogUploadValidationError -> {
                            logger.info("Error validating worklog (${uploadStatus.localLog.id}) for upload due to: ${uploadStatus.worklogValidateError}")
                            Observable.empty()
                        }
                    }
                }
                .toList()
                .toCompletable()
    }

    /**
     * Uploads provided [Log] if it's eligible for upload
     */
    fun uploadLog(
            fetchTime: DateTime,
            log: Log
    ): Single<WorklogUploadState> {
        return Single.just(log.isEligibleForUpload())
                .flatMap { uploadValidatorState ->
                    when (uploadValidatorState) {
                        is WorklogValid -> {
                            jiraWorklogInteractor.uploadWorklog(fetchTime, log)
                                    .map<WorklogUploadState> { WorklogUploadSuccess(it) }
                                    .onErrorResumeNext { error -> Single.just(WorklogUploadError(log, error)) }
                        }
                        else -> Single.just(WorklogUploadValidationError(log, uploadValidatorState))
                    }
                }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}