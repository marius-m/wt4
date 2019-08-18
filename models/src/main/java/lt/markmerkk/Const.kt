package lt.markmerkk

object Const {
    const val NO_ID = 0L
    const val TRUE = 1
    const val FALSE = 0
    const val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved, done))" +
            " AND " +
            "(assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())"
    const val TIMEOUT_1s = 1000L
    const val TIMEOUT_2s = 2000L
}

fun Int.toBoolean(): Boolean = this == 1
fun Boolean.toInt(): Int = if (this) 1 else 0
fun Boolean.toByte(): Byte = (if (this) 1 else 0).toByte()
fun Byte.toBoolean(): Boolean = this == 1.toByte()
