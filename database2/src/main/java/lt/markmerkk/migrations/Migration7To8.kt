package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration7To8(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 7
    override val migrateVersionTo: Int = 8

    override fun migrate(conn: Connection) {
        val sql = "" +
                "CREATE TABLE `ticket_use_history` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `code_project` VARCHAR(50) DEFAULT '' NOT NULL,\n" +
                "  `code_number` VARCHAR(50) DEFAULT '' NOT NULL,\n" +
                "  `code` VARCHAR(50) DEFAULT '' NOT NULL,\n" +
                "  `lastUsed` BIGINT NOT NULL DEFAULT 0\n" +
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