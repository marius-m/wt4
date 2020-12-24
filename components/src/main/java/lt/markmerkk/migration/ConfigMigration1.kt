package lt.markmerkk.migration

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import java.io.File

/**
 * Migration that would move configuration by the app flavor.
 * This is needed, as both flavors normally "inherit" its configuration and its changes
 * thus resulting in incorrect application when both applications are launched.
 *
 * For instance, when both apps are launched and are using different jiras at the same time
 */
class ConfigMigration1(
        private val versionCode: Int,
        private val configDirLegacy: File,
        private val configDirFull: File,
        private val l: Logger
) : ConfigPathMigrator.ConfigMigration {

    override fun isMigrationNeeded(): Boolean {
        val configCountInFull = ConfigUtils.listConfigs(configDirFull)
                .count()
        // Only one version would trigger migration + full path should be empty
        l.info("Checking if configs need migration1: [configCountInFull <= 1($configCountInFull)] " +
                "/ [versionCode <= 66($versionCode)]")
        return configCountInFull <= 1 && versionCode <= 66
    }

    override fun doMigration() {
        l.info("Triggering migration1...")
        ConfigUtils.listConfigs(configDirLegacy)
                .filter { it != configDirFull }
                .filterNot { it.isDirectory && it.name == "logs" } // ignore log directory
                .forEach {
                    try {
                        FileUtils.copyToDirectory(it, configDirFull)
                    } catch (e: Exception) {
                        l.warn("Error doing migration1", e)
                    }
                }
        l.info("Migration complete!")
    }

}