package lt.markmerkk.worklogs

import lt.markmerkk.entities.Log

/**
 * Responsible for making sure that worklog is eligible for upload
 */
object WorklogUploadValidator {}

fun Log.isEligibleForUpload(): WorklogValidatorState {
    return when {
        remoteData != null -> WorklogInvalidAlreadyRemote()
        comment.isEmpty() -> WorklogInvalidNoComment()
        code.isEmpty() -> WorklogInvalidNoTicketCode()
        time.duration.standardMinutes <= 0 -> WorklogInvalidDurationTooLittle()
        else -> WorklogValid()
    }
}

sealed class WorklogValidatorState(val errorMessage: String)
class WorklogValid : WorklogValidatorState("")
class WorklogInvalidNoTicketCode : WorklogValidatorState("No ticket code")
class WorklogInvalidNoComment : WorklogValidatorState("No comment")
class WorklogInvalidDurationTooLittle : WorklogValidatorState("Duration must be at least 1 minute")
class WorklogInvalidAlreadyRemote : WorklogValidatorState("Worklog already marked as remote")

sealed class WorklogUploadState
data class WorklogUploadSuccess(val remoteLog: Log) : WorklogUploadState()
data class WorklogUploadError(val localLog: Log, val error: Throwable) : WorklogUploadState()
data class WorklogUploadValidationError(
        val localLog: Log,
        val worklogValidateError: WorklogValidatorState
) : WorklogUploadState()
