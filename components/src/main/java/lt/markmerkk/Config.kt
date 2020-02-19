package lt.markmerkk

import lt.markmerkk.utils.ConfigSetSettings

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
data class Config(
        val debug: Boolean = false,
        val versionName: String = "Undefined",
        val versionCode: Int = -1,
        val gaKey: String,
        private val configPathProvider: ConfigPathProvider,
        private val configSetSettings: ConfigSetSettings
) {

    val appName: String = "WT4"

    val configInCache by lazy {
        configSetSettings.load()
        configSetSettings.save()
        configPathProvider.absolutePathWithMissingFolderCreate(generateRelativePath())
    }

    val cfgPath: String = configInCache

    fun generateRelativePath(): String {
        var path = configPathProvider.userHome() +
                "/.${configPathProvider.configDefault()}/"
        val currentConfig = configSetSettings.currentConfig()
        if (!currentConfig.isEmpty()) {
            path += "$currentConfig/"
        }
        return path
    }

    override fun toString(): String {
        return "Config: DEBUG=$debug; versionName=$versionName; versionCode=$versionCode; gaKey=$gaKey"
    }
}

