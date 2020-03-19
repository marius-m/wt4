package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration9To10(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 9
    override val migrateVersionTo: Int = 10

    override fun migrate(conn: Connection) {
        // No changes are needed, due to consistency in debug data, adding such a migration
        val sql = "ALTER TABLE `worklog` ADD COLUMN author TEXT NOT NULL DEFAULT '';"
        try {
            val createTableStatement = conn.createStatement()
            logger.debug("Adding new column to `worklog`")
            createTableStatement.execute(sql)
        } catch (e: SQLException) {
            logger.error("Error executing migration from $migrateVersionFrom to $migrateVersionTo", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}