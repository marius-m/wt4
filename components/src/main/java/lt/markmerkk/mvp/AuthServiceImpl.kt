package lt.markmerkk.mvp

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

    override fun onAttach() { }

    override fun onDetach() {
        subscriptions.forEach { it.unsubscribe() }
    }

    override fun testLogin(
            hostname: String,
            username: String,
            password: String
    ) {
        authInteractor.jiraTestValidConnection(
                hostname,
                username,
                password
        )
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnTerminate { view.hideProgress() }
                .subscribe({
                    if (it) {
                        view.showAuthSuccess()
                    }
                }, {
                    handleError(it)
                }).let { subscriptions.add(it) }
    }

    override fun debug() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun handleError(error: Throwable) {
        if (error.message != null && error.message!!.contains("401 Unauthorized")) {
            view.showAuthFailUnauthorised(error)
            return
        }
        if (error.message != null && error.message!!.contains("404 Not Found")) {
            view.showAuthFailInvalidHostname(error)
            return
        }
        view.showAuthFailInvalidUndefined(error)
    }

}