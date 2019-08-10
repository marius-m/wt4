package lt.markmerkk.interactors

import lt.markmerkk.Tags
import net.rcarz.jiraclient.RestException
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.lang.Exception

class AuthServiceImpl(
        private val view: AuthService.View,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler,
        private val authInteractor: AuthService.AuthInteractor
) : AuthService {

    private var testConnectionSubscription: Subscription? = null

    override fun onAttach() {}

    override fun onDetach() {
        testConnectionSubscription?.unsubscribe()
    }

    override fun testLogin(
            hostname: String,
            username: String,
            password: String
    ) {
        testConnectionSubscription?.unsubscribe()
        testConnectionSubscription = Observable.defer { authInteractor.jiraTestValidConnection(hostname, username, password) }
                .flatMap {
                    logger.info("Success logging in!")
                    Observable.just(AuthService.AuthResult.SUCCESS)
                }
                .onErrorResumeNext { Observable.just(handleError(it)) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnTerminate { view.hideProgress() }
                .subscribe({
                    view.showAuthResult(it)
                }, {
                    logger.error("Error checking status of jira validation")
                })
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
        fun handleError(error: Throwable): AuthService.AuthResult {
            if (error is IllegalArgumentException) {
                logger.warn(error.message.toString())
                return AuthService.AuthResult.ERROR_EMPTY_FIELDS
            }
            val restException = error.findRestException()
            if (restException == null) {
                logger.warn("Undefined error: $error")
                return AuthService.AuthResult.ERROR_UNDEFINED
            }
            return when (restException.httpStatusCode) {
                401 -> {
                    logger.warn("Error accessing using credentials! Status:${restException.httpStatusCode}") // lots of noise
                    AuthService.AuthResult.ERROR_UNAUTHORISED
                }
                404 -> {
                    logger.warn("Host or endpoint not found: Status: ${restException.httpStatusCode} / ${restException.httpResult}")
                    AuthService.AuthResult.ERROR_INVALID_HOSTNAME
                }
                else -> {
                    logger.warn("Undefined error!", error)
                    AuthService.AuthResult.ERROR_UNDEFINED
                }
            }
        }
    }

}

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

