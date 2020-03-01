package lt.markmerkk

import lt.markmerkk.utils.ConfigSetSettings

data class Config(
        val debug: Boolean = false,
        val versionName: String = "Undefined",
        val versionCode: Int = -1,
        val gaKey: String,
        private val configPathProvider: ConfigPathProvider,
        private val configSetSettings: ConfigSetSettings
) {

    val appName: String = "WT4"

    private val configInCache by lazy {
        configSetSettings.load()
        configSetSettings.save()
        configPathProvider.absolutePathWithMissingFolderCreate(profilePath())
    }

    val cfgPath: String = configInCache

    fun basePath(): String {
        return configPathProvider.userHome() +
                "/.${configPathProvider.configDefault()}/"
    }

    fun profilePath(): String {
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

