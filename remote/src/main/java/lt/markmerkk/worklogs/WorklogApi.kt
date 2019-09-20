package lt.markmerkk.worklogs

import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.exceptions.AuthException
import lt.markmerkk.utils.LogFormatters
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.RestException
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Completable
import rx.Observable
import rx.Single
import java.net.UnknownHostException

class WorklogApi(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraWorklogInteractor: JiraWorklogInteractor,
        private val ticketStorage: TicketStorage,
        private val worklogStorage: WorklogStorage
) {

    /**
     * Fetches and stores remote [Log] into database
     * Returns all the fresh worklogs from the network
     * @throws AuthException whenever authorization fails (thrown in stream)
     */
    fun fetchLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Single<List<Log>> {
        val startFormat = LogFormatters.shortFormatDate.print(start)
        val endFormat = LogFormatters.shortFormatDate.print(end)
        val jql = "(worklogDate >= \"$startFormat\" && worklogDate <= \"$endFormat\" && worklogAuthor = currentUser())"
        return Completable.fromAction { logger.info("--- START: Fetching logs ($start / $end)... ---") }
                .andThen(
                        jiraWorklogInteractor.searchWorlogs(
                                fetchTime = fetchTime,
                                jql = jql,
                                startDate = start,
                                endDate = end
                        )
                )
                .onErrorResumeNext { error ->
                    logger.warnWithJiraException(
                            "Error fetching remote worklogs",
                            error
                    )
                    val isAuthException = error.isAuthException()
                    val noNetworkException = error.findException<UnknownHostException>()
                    val jiraException = error.findException<JiraException>()
                    when {
                        noNetworkException != null -> {
                            Observable.error(noNetworkException)
                        }
                        isAuthException -> {
                            jiraClientProvider.markAsError()
                            Observable.error(AuthException(error))
                        }
                        jiraException != null -> {
                            Observable.error(jiraException)
                        }
                        else -> Observable.empty()
                    }
                }
                .map { (ticket, worklogs) ->
                    ticketStorage.insertOrUpdateSync(ticket)
                    worklogs.forEach {
                        worklogStorage.insertOrUpdateSync(it)
                    }
                    worklogs
                }
                .flatMap { Observable.from(it) }
                .toList()
                .doOnCompleted { logger.info("--- END ---") }
                .take(1)
                .toSingle()
    }

    /**
     * Removes all unknown remote [Log]'s from database. 'Unknown' - exist in jira and does not exist in our
     * database as a remote log.
     */
    fun deleteUnknownLogs(
            apiWorklogsAsStream: Single<List<Log>>,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        return Completable.fromAction { logger.info("--- START: Deleting unknown logs... ---") }
                .andThen(
                        worklogStorage.loadWorklogs(start, end)
                                .zipWith(
                                        apiWorklogsAsStream,
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
                .doOnCompleted { logger.info("--- END ---") }
    }

    /**
     * Removes all [Log] when are marked for deletion [Log.remoteData.isDelete]
     * @throws AuthException when authorization fails (thrown in stream)
     */
    fun deleteMarkedLogs(start: LocalDate, end: LocalDate): Completable {
        return Completable.fromAction { logger.info("--- START: Deleting logs marked for deletion ($start to $end)... ---") }
                .andThen(worklogStorage.loadWorklogs(start, end))
                .flatMapObservable { Observable.from(it) }
                .filter { it.isMarkedForDeletion }
                .flatMapSingle { worklog ->
                    logger.warn("Trying to delete ${worklog.toStringShort()}")
                    jiraWorklogInteractor.delete(worklog)
                            .onErrorResumeNext { error ->
                                logger.warnWithJiraException("Error trying to delete ${worklog.toStringShort()}", error)
                                if (error.isAuthException()) {
                                    jiraClientProvider.markAsError()
                                    Single.error(AuthException(error))
                                } else {
                                    val remoteId = worklog.remoteData?.remoteId ?: Const.NO_ID
                                    Single.just(remoteId)
                                }
                            }.doOnSuccess { worklogStorage.hardDeleteRemoteSync(it) }
                }
                .toList()
                .toCompletable()
                .doOnCompleted { logger.info("--- END ---") }
    }

    /**
     * Uploads all local logs for the provided time gap
     * @throws AuthException whenever authorization fails (thrown in stream)
     */
    fun uploadLogs(
            fetchTime: DateTime,
            start: LocalDate,
            end: LocalDate
    ): Completable {
        return Completable.fromAction { logger.info("--- START: Uploading local worklogs... ---") }
                .andThen(worklogStorage.loadWorklogs(start, end))
                .flatMapObservable { Observable.from(it) }
                .flatMapSingle { uploadLog(fetchTime, it) }
                .flatMap { uploadStatus ->
                    when (uploadStatus) {
                        is WorklogUploadSuccess -> {
                            logger.info("Success uploading ${uploadStatus.remoteLog.toStringShort()}")
                            worklogStorage
                                    .insertOrUpdate(uploadStatus.remoteLog)
                                    .toObservable()
                        }
                        is WorklogUploadError -> {
                            logger.warnWithJiraException(
                                    "Error uploading ${uploadStatus.localLog.toStringShort()}",
                                    uploadStatus.error
                            )
                            Observable.empty()
                        }
                        is WorklogUploadValidationError -> {
                            logger.info("${uploadStatus.localLog.toStringShort()} not eligable for upload: ${uploadStatus.worklogValidateError.errorMessage}")
                            Observable.empty()
                        }
                    }
                }
                .toList()
                .toCompletable()
                .doOnCompleted { logger.info("--- END ---") }
    }

    /**
     * Uploads provided [Log] if it's eligible for upload
     * @throws AuthException whenever authorization fails (thrown in stream)
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
                                    .onErrorResumeNext { error ->
                                        if (error.isAuthException()) {
                                            val logWithErrorMessage = log.appendSystemNote("Cannot upload: " +
                                                    "Authorization error. " +
                                                    "Check your connection with JIRA")
                                            worklogStorage.updateSync(logWithErrorMessage)
                                            Single.error(AuthException(error))
                                        } else {
                                            val errorAsRestException = error.findException<RestException>()
                                            val errorAsJiraException = error.findException<JiraException>()
                                            val logWithErrorMessage = when {
                                                errorAsRestException != null -> {
                                                    log.appendSystemNote("Cannot upload: JIRA Error (${errorAsRestException.httpStatusCode}) ${errorAsRestException.httpResult}")
                                                }
                                                errorAsJiraException != null -> {
                                                    val errorAsJira = error as JiraException
                                                    log.appendSystemNote("Cannot upload: '${errorAsJira.message}'")
                                                }
                                                else -> log.appendSystemNote("Cannot upload: Unrecognized error, check 'Account settings' for more info")
                                            }
                                            worklogStorage.updateSync(logWithErrorMessage)
                                            Single.just(WorklogUploadError(logWithErrorMessage, error))
                                        }
                                    }
                        }
                        is WorklogInvalidNoTicketCode,
                        is WorklogInvalidNoComment,
                        is WorklogInvalidDurationTooLittle -> {
                            val logWithErrorMessage = log.appendSystemNote("Cannot upload: ${uploadValidatorState.errorMessage}")
                            val worklogUploadValidationError = WorklogUploadValidationError(logWithErrorMessage, uploadValidatorState)
                            worklogStorage.updateSync(logWithErrorMessage)
                            Single.just(worklogUploadValidationError)
                        }
                        is WorklogInvalidAlreadyRemote -> {
                            Single.just(WorklogUploadValidationError(log, uploadValidatorState))
                        }
                    }
                }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}