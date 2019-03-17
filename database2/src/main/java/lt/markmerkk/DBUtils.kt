package lt.markmerkk

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Connection

object DBUtils {

    fun userVersion(conn: Connection): Int {
        return DSL.using(conn, SQLDialect.SQLITE)
                .fetch("PRAGMA user_version;")
                .getValue(0, "user_version") as Int
    }

    fun renewUserVersion(conn: Connection, newVersion: Int) {
        DSL.using(conn, SQLDialect.SQLITE)
                .execute("PRAGMA user_version = $newVersion;")
    }

}