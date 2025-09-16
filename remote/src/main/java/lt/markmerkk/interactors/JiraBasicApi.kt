package lt.markmerkk.interactors

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraUser
import rx.Single

class JiraBasicApi(
        private val jiraClientProvider: JiraClientProvider
) {

    fun jiraUser(): Single<JiraUser> {
        return Single.defer {
            val clientUser = jiraClientProvider.client()
                    .fetchCurrentUser()
            val jiraUser = JiraUser(
                    name = clientUser.name ?: "",
                    displayName = clientUser.displayName ?: "",
                    email = clientUser.email ?: "",
            )
            Single.just(jiraUser)
        }
    }

}
