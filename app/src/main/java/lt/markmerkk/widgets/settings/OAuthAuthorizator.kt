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
        val jiraUser = userSettings.jiraUser()
        if (jiraUser.isEmpty()
                || userSettings.jiraOAuthCreds().isEmpty()) {
            view.renderView(AuthViewModel(
                    showContainerWebview = false,
                    showContainerStatus = true,
                    showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                    textStatus = "No user connected!",
                    showButtonSetupNew = true
            ))
        } else {
            view.renderView(AuthViewModel(
                    showContainerWebview = false,
                    showContainerStatus = true,
                    showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                    textStatus = "Welcome '${jiraUser.displayName}'!",
                    showButtonSetupNew = true
            ))
        }
        view.resetWeb()
    }

    fun onDetach() {
        subsCheckConnection?.unsubscribe()
        subsAuth1?.unsubscribe()
        subsAuth2?.unsubscribe()
        view.resetWeb()
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
                            textStatus = "Welcome '${it.displayName}'!",
                            showButtonSetupNew = false
                    ))
                    view.accountReady()
                }, {
                    logger.warn("Error trying to establish connection!", it)
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                            textStatus = "Error connecting to JIRA. Press 'Show logs' for more details!",
                            showButtonSetupNew = true
                    ))
                })
    }

    fun setupAuthStep1() {
        logger.debug("Authorization STEP 1")
        subsAuth1?.unsubscribe()
        userSettings.resetUserData()
        subsAuth1 = oAuthInteractor.generateAuthUrl()
                .subscribeOn(ioScheduler)
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
                            textStatus = "",
                            showButtonSetupNew = false
                    ))
                    view.loadAuthWeb(it)
                }, {
                    logger.debug("Error trying to generate token for authorization", it)
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                            textStatus = "Error generating JIRA token. Press 'Show logs' for more info!",
                            showButtonSetupNew = true
                    ))
                })
    }

    fun setupAuthStep2(accessTokenKey: String) {
        subsAuth2?.unsubscribe()
        view.resetWeb()
        view.renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                textStatus = "Finishing up authorization...",
                showButtonSetupNew = false
        ))
        if (accessTokenKey.isEmpty()) {
            userSettings.resetUserData()
            logger.debug("Error getting access token key")
            view.renderView(AuthViewModel(
                    showContainerWebview = false,
                    showContainerStatus = true,
                    showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                    textStatus = "Error generating JIRA token. Press 'Show logs' for more info",
                    showButtonSetupNew = true
            ))
            return
        }
        logger.debug("Success finding '$accessTokenKey'")
        subsAuth2 = oAuthInteractor.generateToken(accessTokenKey)
                .flatMap {
                    userSettings.changeOAuthCreds(
                            tokenSecret = it.tokenSecret,
                            accessKey = it.accessKey
                    )
                    Single.just(jiraClientProvider.newClient())
                }.flatMap { jiraApi.jiraUser() }
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
                            textStatus = "Welcome '${it.displayName}'!",
                            showButtonSetupNew = false
                    ))
                    view.accountReady()
                }, {
                    logger.warn("Error finalizing JIRA token export!", it)
                    view.renderView(AuthViewModel(
                            showContainerWebview = false,
                            showContainerStatus = true,
                            showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                            textStatus = "Error generating JIRA token. Press 'Show logs' for more info",
                            showButtonSetupNew = true
                    ))
                })
    }

    interface View {
        fun accountReady()
        fun renderView(authViewModel: AuthViewModel)
        fun showProgress()
        fun hideProgress()
        fun loadAuthWeb(url: String)
        fun resetWeb()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)!!
    }

}
