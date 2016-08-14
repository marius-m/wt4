package lt.markmerkk

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
object Const {
    val DEFAULT_JQL_WORKLOG_TEMPLATE = "key in workedIssues(\"%s\", \"%s\", \"%s\")"
    val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved)) AND (assignee = currentUser() OR reporter = currentUser())"

//    var DEBUG = true

//    val cfgHome: String by lazy {
//
//    }

}