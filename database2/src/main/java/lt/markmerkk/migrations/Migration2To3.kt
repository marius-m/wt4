package lt.markmerkk.migrations

import lt.markmerkk.DBConnProvider
import lt.markmerkk.Tags
import org.slf4j.LoggerFactory
import java.sql.Connection

class Migration2To3(
        private val database: DBConnProvider
) : DBMigration {

    override val migrateVersionFrom: Int = 2
    override val migrateVersionTo: Int = 3

    override fun migrate(conn: Connection) {
        // No changes are needed, due to consistency in debug data, adding such a migration
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }
}