package lt.markmerkk

import java.io.File
import java.io.IOException

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
object Const {
    val DEFAULT_JQL_WORKLOG_TEMPLATE = "key in workedIssues(\"%s\", \"%s\", \"%s\")"
    val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved)) AND (assignee = currentUser() OR reporter = currentUser())"

    var DEBUG = true

    val cfgHome: String by lazy {
        val home = System.getProperty("user.home")
        try {
            val file = File(home + if (DEBUG) "/.wt4_debug/" else "/.wt4/")
            file.mkdirs()
            file.absolutePath + "/"
        } catch (e: IOException) {
            throw IllegalStateException("Cannot initialize user.home dir!")
        }
    }

}