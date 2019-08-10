package lt.markmerkk.interactors

import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

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
                    logger.info("[INFO] Success logging in!")
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
                    logger.error("[ERROR] Error checking status of jira validation")
                })
    }

    fun handleError(error: Throwable): AuthService.AuthResult {
        if (error is IllegalArgumentException) {
            logger.warn("[WARNING] ${error.message}")
            return AuthService.AuthResult.ERROR_EMPTY_FIELDS
        }
        if (error.message != null && error.message!!.contains("401 Unauthorized")) {
            logger.warn("[WARNING] Unauthorized!")
            return AuthService.AuthResult.ERROR_UNAUTHORISED
        }
        if (error.message != null && error.message!!.contains("404 Not Found")) {
            logger.warn("[WARNING] Hostname not found!")
            return AuthService.AuthResult.ERROR_INVALID_HOSTNAME
        }
        logger.warn("[WARNING] Undefined error!", error)
        return AuthService.AuthResult.ERROR_UNDEFINED
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}