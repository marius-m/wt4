package lt.markmerkk

import net.rcarz.jiraclient.User

fun User?.toJiraUser(): JiraUser {
    if (this == null) {
        return JiraUser.asEmpty()
    }
    return JiraUser(
            name = this.name ?: "",
            displayName = this.displayName ?: "",
            email = this.email ?: "",
            accountId = this.accountId ?: ""
    )
}