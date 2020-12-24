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
                    .currentUser2()
            val jiraUser = JiraUser(
                    name = clientUser.name ?: "",
                    displayName = clientUser.displayName ?: "",
                    email = clientUser.email ?: "",
                    accountId = clientUser.accountId ?: ""
            )
            Single.just(jiraUser)
        }
    }

}
