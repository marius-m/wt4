package lt.markmerkk

import lt.markmerkk.migrations.DBMigration
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory

class MigrationsRunner(
        private val connProvider: DBConnProvider
) {

    fun run(migrations: List<DBMigration>) {
        val conn = connProvider.connect()
        try {
            var dbVersion = DBUtils.userVersion(conn)
            logger.debug("Migrations start")
            logger.debug("Current db version - ${dbVersion}")
            migrations.forEach {
                dbVersion = DBUtils.userVersion(conn)
                val isNeedMigration = it.needMigration(dbVersion)
                if (isNeedMigration) {
                    logger.debug("Migrating from ${it.migrateVersionFrom} .. ${it.migrateVersionTo}")
                    it.migrate(conn)
                    logger.debug("Moving DB version to ${it.migrateVersionTo}")
                    DBUtils.renewUserVersion(conn, it.migrateVersionTo)
                }
            }
            logger.debug("Migrations finished")
        } finally {
            conn.close()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.DB)!!
    }

}