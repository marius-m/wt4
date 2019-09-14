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
                    .currentUser()
            val jiraUser = JiraUser(clientUser.name, clientUser.displayName, clientUser.email)
            Single.just(jiraUser)
        }
    }

}
