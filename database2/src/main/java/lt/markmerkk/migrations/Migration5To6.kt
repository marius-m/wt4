package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration5To6(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 5
    override val migrateVersionTo: Int = 6

    override fun migrate(conn: Connection) {
        val sqlAlter1 = "ALTER TABLE `ticket` ADD COLUMN `parent_code` VARCHAR(50) DEFAULT '' NOT NULL"
        val sqlAlter2 = "ALTER TABLE `ticket` ADD COLUMN `status` VARCHAR(100) DEFAULT '' NOT NULL"
        try {
            logger.debug("Altering table: $sqlAlter1")
            val alterStatement1 = conn
                    .createStatement()
                    .execute(sqlAlter1)
            logger.debug("Altering table: $sqlAlter2")
            val alterStatement2 = conn
                    .createStatement()
                    .execute(sqlAlter2)
        } catch (e: SQLException) {
            logger.error("Error executing migration from $migrateVersionFrom to $migrateVersionTo", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}