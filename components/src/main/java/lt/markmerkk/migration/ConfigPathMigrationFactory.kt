package lt.markmerkk.migration

import java.io.File

object ConfigPathMigrationFactory {
    fun create(
            versionCode: Int,
            configDirLegacy: File,
            configDirFull: File
    ): List<ConfigPathMigrator.ConfigMigration> {
        return listOf(
                ConfigMigration1(versionCode, configDirLegacy, configDirFull, ConfigPathMigrator.l)
        )
    }
}