package lt.markmerkk.migration

import java.io.File

object ConfigUtils {
    fun listConfigs(configDir: File): List<File> {
        return if (configDir.exists() && configDir.isDirectory) {
            configDir.listFiles()?.asList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}