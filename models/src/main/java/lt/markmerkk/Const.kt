package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
object Const {
    val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved, done))" +
            " AND " +
            "(assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())"
}