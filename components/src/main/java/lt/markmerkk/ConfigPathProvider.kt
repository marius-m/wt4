package lt.markmerkk

import lt.markmerkk.migration.ConfigPathMigrationFactory
import lt.markmerkk.migration.ConfigPathMigrator
import java.io.File

/**
 * Responsible for providing paths to root config places
 */
class ConfigPathProvider(
        private val debug: Boolean,
        private val versionCode: Int,
        private val appFlavor: String
) {

    val configDefault: String by lazy { configDefault() }
    val userHome: String by lazy { userHome() }
    val fullAppDir: File by lazy { fullAppDir() }

    init {
        val configPathMigrator = ConfigPathMigrator(
                migrations = ConfigPathMigrationFactory.create(
                        versionCode = versionCode,
                        configDirLegacy = legacyAppDir(),
                        configDirFull = fullAppDir()
                )
        )
        configPathMigrator
                .runMigrations()
    }

    /**
     * Path to root config dir
     * Ex.: '~/.wt4'; '~/.wt4_debug'
     */
    private fun configDefault(): String {
        val wtAppPath = System.getProperty(Const.KEY_SYS_WT_APP_PATH)
        return if (!wtAppPath.isNullOrEmpty()) {
            wtAppPath
        } else {
            Const.DEFAULT_SYS_WT_APP_PATH
        }
    }

    /**
     * Root home dir
     */
    private fun userHome(): String {
        val wtRoot = System.getProperty(Const.KEY_SYS_WT_ROOT)
        return if (!wtRoot.isNullOrEmpty()) {
            wtRoot
        } else {
            System.getProperty(Const.DEFAULT_SYS_WT_ROOT)
        }
    }

    /**
     * Full path of configuration
     */
    private fun fullAppDir(): File {
        val appDir = File("${userHome()}${File.separator}.${configDefault()}-$appFlavor")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

    /**
     * Root path legacy directory of the app
     */
    private fun legacyAppDir(): File {
        val appDir = File("${userHome()}${File.separator}.${configDefault()}")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

}