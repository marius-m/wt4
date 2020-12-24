package lt.markmerkk.migration

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

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

class ConfigMigration1(
        private val versionCode: Int,
        private val configDirRoot: File,
        private val configDirFull: File,
        private val l: Logger
) : ConfigPathMigrator.ConfigMigration {

    override fun isMigrationNeeded(): Boolean {
        val configCountInFull = ConfigUtils
                .listConfigs(configDirFull)
                .count()
        // Only one version would trigger migration + full path should be empty
        l.info("Checking if configs need migration1: [configCountInFull <= 1($configCountInFull)] " +
                "/ [versionCode <= 66($versionCode)]")
        return configCountInFull <= 1 && versionCode <= 66
    }

    override fun doMigration() {
        l.info("Triggering migration1...")
        ConfigUtils.listConfigs(configDirRoot)
                .filter { it != configDirFull }
                .filterNot { it.isDirectory && it.name == "logs" } // ignore log directory
                .forEach {
                    try {
                        FileUtils.moveToDirectory(it, configDirFull, false)
                    } catch (e: Exception) {
                        l.warn("Error doing migration1", e)
                    }
                }
        l.info("Migration complete!")
    }

}

object ConfigUtils {
    fun listConfigs(configDir: File): List<File> {
        return if (configDir.exists() && configDir.isDirectory) {
            configDir.listFiles()?.asList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}

object ConfigPathMigrationFactory {
    fun create(
            versionCode: Int,
            configDirRoot: File,
            configDirFull: File
    ): List<ConfigPathMigrator.ConfigMigration> {
        return listOf(
                ConfigMigration1(versionCode, configDirRoot, configDirFull, ConfigPathMigrator.l)
        )
    }
}
