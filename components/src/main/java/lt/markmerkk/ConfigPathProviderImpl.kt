package lt.markmerkk

import java.io.File
import java.io.IOException

class ConfigPathProviderImpl(
        private val debug: Boolean
) : ConfigPathProvider {

    override fun configDefault(): String {
        val wtAppPath = System.getProperty(Const.KEY_SYS_WT_APP_PATH)
        return if (!wtAppPath.isNullOrEmpty()) {
            wtAppPath
        } else {
            Const.DEFAULT_SYS_WT_APP_PATH
        }
    }

    override fun userHome(): String {
        val wtRoot = System.getProperty(Const.KEY_SYS_WT_ROOT)
        return if (!wtRoot.isNullOrEmpty()) {
            wtRoot
        } else {
            System.getProperty(Const.DEFAULT_SYS_WT_ROOT)
        }
    }

    override fun fullAppDir(): File {
        val appDir = File("${userHome()}${File.separator}.${configDefault()}")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

}