package lt.markmerkk

import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.RestException
import org.slf4j.Logger

object JiraNetworkUtils { }

inline fun <reified T: Exception> Throwable.findException(): T? {
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
                if (it is T) {
                    it
                } else {
                    null
                }
            }.firstOrNull()
}

fun Throwable.isAuthException(): Boolean {
    val restAuthException = findException<RestException>()?.httpStatusCode == 401
    val isAuthException = findException<AuthException>() != null
    return restAuthException
            || isAuthException
}

fun Logger.warnWithJiraException(message: String, throwable: Throwable) {
    val authException = throwable.findException<AuthException>()
    val restException = throwable.findException<RestException>()
    when {
        authException != null -> this.warn("$message / Authorization error")
        restException != null && restException.httpStatusCode == 401 -> this.warn("$message / Authorization error (401)!")
        restException != null -> this.warn("$message / Rest error (${restException.httpStatusCode}): ${restException.httpResult}")
        throwable is JiraException -> this.warn("$message / Jira error: ${throwable.message}")
        else -> this.warn(message, throwable)
    }
}
