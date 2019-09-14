package lt.markmerkk.exceptions

import lt.markmerkk.findException
import net.rcarz.jiraclient.RestException

/**
 * Marks as authorization exception, should halt all further works on the
 * network
 */
class AuthException(
        throwable: Throwable
): RuntimeException("Authorization failure", throwable) {

    private val restException: RestException? = throwable.findException()
    override val message: String? = when {
        restException != null -> "Authorization failure (${restException.httpStatusCode})!"
        else -> super.message
    }

}