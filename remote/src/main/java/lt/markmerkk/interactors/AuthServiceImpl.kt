package lt.markmerkk.interactors

import lt.markmerkk.Tags
import lt.markmerkk.UserSettings
import lt.markmerkk.findException
import net.rcarz.jiraclient.RestException
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Single
import rx.Subscription

class AuthServiceImpl(
        private val view: AuthService.View,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler,
        private val authInteractor: AuthService.AuthInteractor,
        private val userSettings: UserSettings
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
        testConnectionSubscription = authInteractor.jiraTestValidConnection(hostname, username, password)
                .flatMap {
                    userSettings.changeJiraUser(it.name, it.email, it.displayName)
                    logger.info("Success logging in!")
                    Single.just(AuthService.AuthResult.SUCCESS)
                }
                .doOnError { userSettings.resetUserData() }
                .onErrorResumeNext { Single.just(handleError(it)) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doAfterTerminate { view.hideProgress() }
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
            val restException = error.findException<RestException>()
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

