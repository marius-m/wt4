package lt.markmerkk.interactors

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraUser
import lt.markmerkk.UserSettings
import rx.Observable
import rx.Single

class AuthInteractorImpl(
        private val jiraClientProvider: JiraClientProvider,
        private val jiraBasicApi: JiraBasicApi,
        private val userSettings: UserSettings
) : AuthService.AuthInteractor {
    override fun jiraTestValidConnection(
            hostname: String,
            username: String,
            password: String
    ): Single<JiraUser> {
        userSettings.changeBasicCreds(hostname, username, password)
        return Single.defer { Single.just(jiraClientProvider.newClient()) }
                .flatMap { jiraBasicApi.jiraUser() }
    }
}