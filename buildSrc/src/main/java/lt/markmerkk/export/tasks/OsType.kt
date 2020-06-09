package lt.markmerkk.export.tasks

import org.apache.tools.ant.taskdefs.condition.Os

enum class OsType {
    UNKNOWN,
    MAC,
    LINUX,
    WINDOWS,
    ;

    companion object {
        fun get(): OsType {
            return when {
                Os.isFamily(Os.FAMILY_MAC) -> MAC
                Os.isFamily(Os.FAMILY_UNIX) -> LINUX
                Os.isFamily(Os.FAMILY_WINDOWS) -> WINDOWS
                else -> UNKNOWN
            }
        }
    }
}
