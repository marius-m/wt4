package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
object Const {
    const val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved, done))" +
            " AND " +
            "(assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())"
    const val TIMEOUT_1s = 1000L
    const val TIMEOUT_2s = 2000L
}