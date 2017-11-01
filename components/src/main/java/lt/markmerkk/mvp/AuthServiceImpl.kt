package lt.markmerkk.mvp

import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2017-09-16
 */
class AuthServiceImpl(
        private val view: AuthService.View,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler,
        private val authInteractor: AuthService.AuthInteractor
) : AuthService {

    private val subscriptions = mutableListOf<Subscription>()

    override fun onAttach() {}

    override fun onDetach() {
        subscriptions.forEach { it.unsubscribe() }
    }

    override fun testLogin(
            hostname: String,
            username: String,
            password: String
    ) {
        authInteractor.jiraTestValidConnection(hostname, username, password)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnTerminate { view.hideProgress() }
                .subscribe({
                    if (it) {
                        view.showAuthResult(AuthService.AuthResult.SUCCESS)
                    }
                }, {
                    handleError(it)
                }).let { subscriptions.add(it) }
    }

    override fun debug() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun handleError(error: Throwable) {
        logger.warn("[WARNING] ", error)
        if (error is IllegalArgumentException) {
            view.showAuthResult(AuthService.AuthResult.ERROR_EMPTY_FIELDS)
            return
        }
        if (error.message != null && error.message!!.contains("401 Unauthorized")) {
            view.showAuthResult(AuthService.AuthResult.ERROR_UNAUTHORISED)
            return
        }
        if (error.message != null && error.message!!.contains("404 Not Found")) {
            view.showAuthResult(AuthService.AuthResult.ERROR_INVALID_HOSTNAME)
            return
        }
        view.showAuthResult(AuthService.AuthResult.ERROR_UNDEFINED)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)!!
    }

}