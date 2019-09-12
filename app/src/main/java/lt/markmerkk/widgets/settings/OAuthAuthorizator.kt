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
        webview.reset()
        if (userSettings.jiraUser().isEmpty()
                || userSettings.jiraOAuthCreds().isEmpty()) {
            view.renderView(AuthViewModel(
                    showContainerWebview = false,
                    showContainerStatus = true,
                    showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                    showButtonSetUp = true,
                    textStatus = "No user connected! Press 'Set-up' to setup your connection!",
                    textHeader = ""
            ))
        } else {
            view.renderView(AuthViewModel(
                    showContainerWebview = false,
                    showContainerStatus = true,
                    showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                    showButtonSetUp = true,
                    textStatus = "User 'username' connected! Press on face to check connection status or 'Set-up' for establishing new connection",
                    textHeader = ""
            ))
        }
    }

    fun onDetach() {
        subsCheckConnection?.unsubscribe()
        subsAuth1?.unsubscribe()
        subsAuth2?.unsubscribe()
        webview.reset()
    }

    fun checkAuth() {
        subsCheckConnection?.unsubscribe()
        subsCheckConnection = jiraApi.jiraUser()
                .doOnSuccess { userSettings.changeJiraUser(it.name, it.email, it.displayName) }
                .doOnError { userSettings.resetUserData() }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doAfterTerminate { view.hideProgress() }
                .subscribe({
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.HAPPY,
                            showButtonSetUp = true,
                            textStatus = "Successfully connected to JIRA with user '${it.displayName}'! Press 'Set-up' to setup a new connection!",
                            textHeader = ""
                    ))
                }, {
                    logger.warn("Error trying to establish connection!", it)
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                            showButtonSetUp = true,
                            textStatus = "Error connecting to JIRA. Press 'Set-up' to establish new connection",
                            textHeader = ""
                    ))
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
                    view.renderView(AuthViewModel(
                            showContainerWebview = true,
                            showContainerStatus = false,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                            showButtonSetUp = false,
                            textStatus = "",
                            textHeader = ""
                    ))
                    view.loadAuth(it)
                }, {
                    logger.debug("Error trying to generate token for authorization", it)
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                            showButtonSetUp = true,
                            textStatus = "Error generating JIRA token. Try again later or press 'Show logs' for more info",
                            textHeader = ""
                    ))
                })
    }

    fun setupAuthStep2(accessTokenKey: String) {
        subsAuth2?.unsubscribe()
        logger.debug("Success finding '$accessTokenKey'")
        subsAuth2 = oAuthInteractor.generateToken(accessTokenKey)
                .flatMap {
                    userSettings.changeOAuthCreds(
                            tokenSecret = it.tokenSecret,
                            accessKey = it.accessKey
                    )
                    jiraApi.jiraUser()
                }
                .doOnSuccess { userSettings.changeJiraUser(it.name, it.email, it.displayName) }
                .doOnError { userSettings.resetUserData() }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doAfterTerminate { view.hideProgress() }
                .subscribe({
                    logger.debug("Success running authorization, found projects: $it")
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.HAPPY,
                            showButtonSetUp = true,
                            textStatus = "Success setting up new user '${it.displayName}'!",
                            textHeader = ""
                    ))
                }, {
                    logger.warn("Error finalizing JIRA token export!", it)
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                            showButtonSetUp = true,
                            textStatus = "Error generating JIRA token. Try again later or press 'Show logs' for more info",
                            textHeader = ""
                    ))
                })
    }

    interface AuthWebView {
        fun loadAuth(url: String)
        fun reset()
    }

    interface View {
        fun renderView(authViewModel: AuthViewModel)
        fun showNeutralState()
        fun showAuthSuccess()
        fun showAuthFailure()
        fun showAuthView()
        fun loadAuth(url: String)
        fun showProgress()
        fun hideProgress()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}
