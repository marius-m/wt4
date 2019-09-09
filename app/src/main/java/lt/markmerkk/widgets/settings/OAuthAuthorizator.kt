package lt.markmerkk.widgets.settings

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Tags
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.JiraBasicApi
import net.rcarz.jiraclient.JiraApi
import org.slf4j.LoggerFactory
import rx.Completable
import rx.Scheduler
import rx.Single
import rx.Subscription

class OAuthAuthorizator(
        private val webview: AuthWebView,
        private val view: View,
        private val oAuthInteractor: OAuthInteractor,
        private val jiraClientProvider: JiraClientProvider,
        private val jiraApi: JiraBasicApi,
        private val userSettings: UserSettings,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subsCheckConnection: Subscription? = null
    private var subsAuth1: Subscription? = null
    private var subsAuth2: Subscription? = null

    fun onAttach() {
        view.showNeutralState()
    }

    fun onDetach() {
        subsCheckConnection?.unsubscribe()
        subsAuth1?.unsubscribe()
        subsAuth2?.unsubscribe()
    }

    fun checkAuth() {
        subsCheckConnection?.unsubscribe()
        subsCheckConnection = jiraApi.jiraUser()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doAfterTerminate { view.hideProgress() }
                .subscribe({
                    view.showAuthSuccess()
                }, {
                    view.showAuthFailure()
                })
    }

    fun setupAuthStep1() {
        logger.debug("Authorization STEP 1")
        subsAuth1?.unsubscribe()
        userSettings.resetUserData()
        subsAuth1 = oAuthInteractor.generateAuthUrl()
                .subscribeOn(uiScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnSuccess { view.hideProgress() }
                .doOnError { view.hideProgress() }
                .subscribe({
                    logger.debug("Loading authorization URL")
                    view.showAuthView()
                    webview.loadAuth(it)
                }, {
                    logger.debug("Error trying to generate token for authorization", it)
                    view.onError(it)
                })
    }

    fun setupAuthStep2(accessTokenKey: String) {
        subsAuth2?.unsubscribe()
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
        fun showNeutralState()
        fun showAuthSuccess()
        fun showAuthFailure()
        fun showAuthView()
        fun showProgress()
        fun hideProgress()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}
