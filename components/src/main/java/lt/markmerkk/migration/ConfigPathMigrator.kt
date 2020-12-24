package lt.markmerkk.migration

import org.slf4j.LoggerFactory

/**
 * Because in app version changes there were many file location migration, thus resulting in
 * new file creating and leaving old files.
 *
 * We would like to keep our old configuration, but moving files in their correct directory.
 *
 * This class will do migration, based on its predicate conditions.
 */
class ConfigPathMigrator(
        private val migrations: List<ConfigMigration>
) {

    fun runMigrations() {
        l.info("Running config migrations")
        migrations
                .filter { it.isMigrationNeeded() }
                .forEach { it.doMigration() }
    }

    interface ConfigMigration {
        fun isMigrationNeeded(): Boolean
        fun doMigration()
    }

    companion object {
        val l = LoggerFactory.getLogger(ConfigPathMigrator::class.java)!!
    }

}
