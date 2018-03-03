package lt.markmerkk.interactors

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.mvp.AuthService
import rx.Completable
import rx.Observable

class AuthInteractorImpl(
        private val jiraClientProvider: JiraClientProvider
) : AuthService.AuthInteractor {
    override fun jiraTestValidConnection(
            hostname: String,
            username: String,
            password: String
    ): Observable<Boolean> {
        return Completable.fromAction { jiraClientProvider.invalidateClient() }
                .andThen(Observable.just(
                        jiraClientProvider.client(
                                hostname,
                                username,
                                password
                        )
                )).flatMap { Observable.just(it.projects) }
                .flatMap {
                    if (it != null) {
                        Observable.just(true)
                    } else {
                        Observable.just(false)
                    }
                }
    }
}