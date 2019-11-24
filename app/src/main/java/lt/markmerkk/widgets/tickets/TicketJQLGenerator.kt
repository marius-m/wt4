package lt.markmerkk.widgets.tickets

import lt.markmerkk.Const

/**
 * Generates JQL based on provided setting details
 */
object TicketJQLGenerator {

    fun generateJQL(
            enabledStatuses: List<String>,
            onlyCurrentUser: Boolean
    ): String {
        val statusQueryPart = if (enabledStatuses.isNotEmpty()) {
            val statusesAsString = enabledStatuses
                    .map { "'$it'" }
                    .joinToString(separator = ",")
            "(status in ($statusesAsString))"
        } else {
            ""
        }
        val currentUserPart = if (onlyCurrentUser) {
            "(assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())"
        } else {
            ""
        }
        val queryParts = listOf(statusQueryPart,  currentUserPart)
                .filter { it.isNotEmpty() }
        return queryParts.joinToString(separator = " AND ")
    }

}