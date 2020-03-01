package lt.markmerkk

import net.rcarz.jiraclient.User

fun User.toJiraUser(): JiraUser {
    return JiraUser(
            name = this.name ?: "",
            displayName = this.displayName ?: "",
            email = this.email ?: "",
            accountId = this.accountId ?: ""
    )
}