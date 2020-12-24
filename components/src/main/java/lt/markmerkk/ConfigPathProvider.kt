package lt.markmerkk

import lt.markmerkk.migration.ConfigPathMigrationFactory
import lt.markmerkk.migration.ConfigPathMigrator
import java.io.File

/**
 * Responsible for providing paths to root config places
 */
class ConfigPathProvider(
        private val debug: Boolean,
        private val appFlavor: String
) {

    init {
        val configPathMigrator = ConfigPathMigrator(
                migrations = ConfigPathMigrationFactory.create(
                        versionCode = BuildConfig.versionCode,
                        configDirRoot = rootAppDir(),
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
    fun configDefault(): String {
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
    fun userHome(): String {
        val wtRoot = System.getProperty(Const.KEY_SYS_WT_ROOT)
        return if (!wtRoot.isNullOrEmpty()) {
            wtRoot
        } else {
            System.getProperty(Const.DEFAULT_SYS_WT_ROOT)
        }
    }

    /**
     * Root path of configuration
     */
    fun rootAppDir(): File {
        val appDir = File("${userHome()}${File.separator}.${configDefault()}")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

    /**
     * Full path of configuration
     */
    fun fullAppDir(): File {
        val appDir = File("${userHome()}${File.separator}.${configDefault()}${File.separator}$appFlavor")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

}