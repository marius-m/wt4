package lt.markmerkk.interactors

import lt.markmerkk.JiraClientProvider
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
                .andThen(
                        jiraClientProvider.clientStream(
                                hostname,
                                username,
                                password
                        )
                )
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