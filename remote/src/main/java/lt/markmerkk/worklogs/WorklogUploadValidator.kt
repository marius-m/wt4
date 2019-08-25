package lt.markmerkk.worklogs

import lt.markmerkk.entities.Log

/**
 * Responsible for making sure that worklog is eligible for upload
 */
object WorklogUploadValidator {}

fun Log.isEligibleForUpload(): WorklogValidatorState {
    return when {
        remoteData != null -> WorklogInvalidAlreadyRemote(this)
        comment.isEmpty() -> WorklogInvalidNoComment(this)
        code.isEmpty() -> WorklogInvalidNoTicketCode(this)
        time.duration.standardMinutes <= 0 -> WorklogInvalidDurationTooLittle(this)
        else -> WorklogValid(this)
    }
}

sealed class WorklogValidatorState(val errorMessage: String)
data class WorklogValid(val localLog: Log) : WorklogValidatorState("")
data class WorklogInvalidNoTicketCode(val log: Log) : WorklogValidatorState("No ticket code")
data class WorklogInvalidNoComment(val log: Log) : WorklogValidatorState("No comment")
data class WorklogInvalidDurationTooLittle(val log: Log) : WorklogValidatorState("Duration must be at least 1 minute")
data class WorklogInvalidAlreadyRemote(val log: Log) : WorklogValidatorState("Worklog already marked as remote")

sealed class WorklogUploadState
data class WorklogUploadSuccess(val remoteLog: Log) : WorklogUploadState()
data class WorklogUploadError(val localLog: Log, val error: Throwable) : WorklogUploadState()
data class WorklogUploadValidationError(
        val localLog: Log,
        val worklogValidateError: WorklogValidatorState
) : WorklogUploadState()
