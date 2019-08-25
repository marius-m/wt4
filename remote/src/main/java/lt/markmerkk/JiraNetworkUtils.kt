package lt.markmerkk

import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.RestException
import org.slf4j.Logger

object JiraNetworkUtils { }

fun Throwable.findRestException(): RestException? {
    val exceptions = mutableListOf<Throwable>()
    var iterableThrowable: Throwable? = this
    do {
        if (iterableThrowable != null) {
            exceptions.add(iterableThrowable)
            iterableThrowable = iterableThrowable.cause
        } else {
            iterableThrowable = null
        }
    } while (iterableThrowable != null)
    return exceptions
            .mapNotNull {
                if (it is RestException) {
                    it
                } else {
                    null
                }
            }.firstOrNull()
}

fun Throwable.isAuthException(): Boolean {
    return findRestException()?.httpStatusCode == 401
}

fun Logger.warnWithJiraException(message: String, throwable: Throwable) {
    val restException = throwable.findRestException()
    when {
        restException != null && restException.httpStatusCode == 401 -> this.warn("$message / Authorization error (401)!")
        restException != null -> this.warn("$message / Rest error (${restException.httpStatusCode}): ${restException.httpResult}")
        throwable is JiraException -> this.warn("$message / Jira error: ${throwable.message}")
        else -> this.warn(message, throwable)
    }
}
