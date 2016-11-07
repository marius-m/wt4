package lt.markmerkk

import lt.markmerkk.utils.ConfigSetSettings
import java.io.File
import java.io.IOException

/**
 * @author mariusmerkevicius
 * @since 2016-11-07
 */
class ConfigPathProviderImpl(
        private val debug: Boolean
) : ConfigPathProvider {

    override fun configDefault(): String {
        return if (debug) {
            "wt4_debug"
        } else {
            "wt4"
        }
    }

    override fun userHome(): String = System.getProperty("user.home")

    override fun absolutePathWithMissingFolderCreate(path: String): String {
        try {
            val file = File(path)
            file.mkdirs()
            return file.absolutePath + "/"
        } catch (e: IOException) {
            throw IllegalStateException("Error initializing config")
        }
    }

}