package lt.markmerkk

import lt.markmerkk.utils.ConfigSetSettings
import java.io.File

data class Config(
        val debug: Boolean = false,
        val versionName: String = "Undefined",
        val versionCode: Int = -1,
        val gaKey: String,
        private val cpp: ConfigPathProvider,
        private val configSetSettings: ConfigSetSettings
) {

    val appName: String = "WT4"

    private val configInCache by lazy {
        configSetSettings.load()
        configSetSettings.save()
        profilePath().absolutePath
    }

    val cfgPath: String = configInCache

    init {
        basePath()
        profilePath()
        tmpPath()
    }

    fun basePath(): File {
        return cpp.fullAppDir()
    }

    fun profilePath(): File {
        val currentConfig = configSetSettings.currentConfig()
        return if (currentConfig.isNotEmpty()) {
            val configDir = File(cpp.fullAppDir(), "${File.separator}${currentConfig}")
            if (!configDir.exists()) {
                configDir.mkdirs()
            }
            configDir
        } else {
            cpp.fullAppDir()
        }
    }

    fun tmpPath(): File {
        val tmpDir = File(cpp.fullAppDir(), "${File.separator}tmp")
        if (!tmpDir.exists()) {
            tmpDir.mkdirs()
        }
        return tmpDir
    }

    fun printPaths(): String {
        return StringBuilder("Config: ")
                .append("DEBUG=$debug; ")
                .append("versionName=$versionName; ")
                .append("versionCode=$versionCode; ")
                .append("gaKey=$gaKey; ")
                .append("wtRoot='${cpp.userHome()}'; ")
                .append("wtAppPath='${cpp.configDefault()}'; ")
                .append("basePath='${basePath()}'; ")
                .append("profilePath='${profilePath()}'; ")
                .append("tmpPath='${tmpPath()}'; ")
                .toString()
    }

}

