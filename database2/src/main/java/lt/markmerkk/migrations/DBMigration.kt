package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import java.sql.Connection

interface DBMigration {
    val migrateVersionFrom: Int
    val migrateVersionTo: Int
    fun needMigration(currentVersion: Int): Boolean = currentVersion == migrateVersionFrom
    fun migrate(conn: Connection)
}