package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class Migration8To9(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 8
    override val migrateVersionTo: Int = 9

    override fun migrate(conn: Connection) {
        val sqlAlter1 = "ALTER TABLE `ticket` ADD COLUMN `assignee` VARCHAR(200) DEFAULT '' NOT NULL"
        val sqlAlter2 = "ALTER TABLE `ticket` ADD COLUMN `reporter` VARCHAR(200) DEFAULT '' NOT NULL"
        val sqlAlter3 = "ALTER TABLE `ticket` ADD COLUMN `is_watching` TINYINT NOT NULL DEFAULT 0"
        try {
            logger.debug("Altering table: $sqlAlter1")
            conn.createStatement()
                    .execute(sqlAlter1)
            logger.debug("Altering table: $sqlAlter2")
            conn.createStatement()
                    .execute(sqlAlter2)
            logger.debug("Altering table: $sqlAlter3")
            conn.createStatement()
                    .execute(sqlAlter3)
        } catch (e: SQLException) {
            logger.error("Error executing migration from $migrateVersionFrom to $migrateVersionTo", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}