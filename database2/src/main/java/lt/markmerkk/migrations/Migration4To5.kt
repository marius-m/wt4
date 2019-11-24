package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration4To5(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 4
    override val migrateVersionTo: Int = 5

    override fun migrate(conn: Connection) {
        val sql = "CREATE TABLE `ticket_status` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `name` VARCHAR(100) DEFAULT '' NOT NULL\n" +
                ");"
        try {
            val createTableStatement = conn.createStatement()
            logger.debug("Creating new table: $sql")
            createTableStatement.execute(sql)
        } catch (e: SQLException) {
            logger.error("Error executing migration from $migrateVersionFrom to $migrateVersionTo", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}