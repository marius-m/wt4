package lt.markmerkk

import java.io.File
import java.io.IOException

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
data class Config(
        val debug: Boolean = false,
        val versionName: String = "Undefined",
        val versionCode: Int = -1,
        val gaKey: String
) {

    val appName: String = "WT4"

    val cfgPath: String
        get() {
            val home = System.getProperty("user.home")
            try {
                val file = File(home + if (debug) "/.wt4_debug/" else "/.wt4/")
                file.mkdirs()
                return file.absolutePath + "/"
            } catch (e: IOException) {
                throw IllegalStateException("Cannot initialize user.home dir!")
            }
        }

    override fun toString(): String {
        return "Config: DEBUG=$debug; versionName=$versionName; versionCode=$versionCode; gaKey=$gaKey"
    }
}