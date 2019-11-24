package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration6To7(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 6
    override val migrateVersionTo: Int = 7

    override fun migrate(conn: Connection) {
        val sqlAlter1 = "ALTER TABLE `ticket_status` ADD COLUMN `enabled` TINYINT DEFAULT 1 NOT NULL"
        try {
            logger.debug("Altering table: $sqlAlter1")
            val alterStatement1 = conn
                    .createStatement()
                    .execute(sqlAlter1)
        } catch (e: SQLException) {
            logger.error("Error executing migration from $migrateVersionFrom to $migrateVersionTo", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}