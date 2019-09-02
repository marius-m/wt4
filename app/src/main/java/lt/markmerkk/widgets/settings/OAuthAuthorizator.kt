package lt.markmerkk.widgets.settings

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Tags
import lt.markmerkk.UserSettings
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Single
import rx.Subscription

class OAuthAuthorizator(
        private val webview: AuthWebView,
        private val view: View,
        private val oAuthInteractor: OAuthInteractor,
        private val jiraClientProvider: JiraClientProvider,
        private val userSettings: UserSettings,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subsAuth1: Subscription? = null
    private var subsAuth2: Subscription? = null

    fun onAttach() {}
    fun onDetach() {
        subsAuth1?.unsubscribe()
        subsAuth2?.unsubscribe()
    }

    fun setupAuthStep1() {
        subsAuth1 = oAuthInteractor.generateAuthUrl()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnSuccess { view.hideProgress() }
                .doOnError { view.hideProgress() }
                .subscribe({
                    webview.loadAuth(it)
                }, {
                    view.onError(it)
                })
    }

    fun setupAuthStep2(accessTokenKey: String) {
        logger.debug("Success finding '$accessTokenKey'")
        subsAuth2 = oAuthInteractor.generateToken(accessTokenKey)
                .flatMap {
                    val jiraClient = jiraClientProvider.newClient()
//                    val userCreds = jiraClient.currentUser()
//                    userSettings.changeOAuthUserCreds(
//                            name = userCreds.name,
//                            email = userCreds.email,
//                            displayName = userCreds.displayName
//                    )
                    Single.just(jiraClient.projects)
                }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doAfterTerminate { view.hideProgress() }
                .subscribe({
                    logger.debug("Success running authorization, found projects: $it")
                })
    }

    interface AuthWebView {
        fun loadAuth(url: String)
    }

    interface View {
        fun showAuthSuccess()
        fun showAuthFailure()
        fun showProgress()
        fun hideProgress()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}
