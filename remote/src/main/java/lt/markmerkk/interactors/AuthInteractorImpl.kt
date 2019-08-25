package lt.markmerkk.interactors

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.UserSettings
import rx.Observable
import rx.Single

class AuthInteractorImpl(
        private val jiraClientProvider: JiraClientProvider,
        private val userSettings: UserSettings
) : AuthService.AuthInteractor {
    override fun jiraTestValidConnection(
            hostname: String,
            username: String,
            password: String
    ): Observable<Boolean> {
        userSettings.host = hostname
        userSettings.username = username
        userSettings.password = password
        return Single.defer { Single.just(jiraClientProvider.newClient()) }
                .flatMapObservable { Observable.just(it.projects) }
                .flatMap {
                    if (it != null) {
                        Observable.just(true)
                    } else {
                        Observable.just(false)
                    }
                }
    }
}