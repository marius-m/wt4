package lt.markmerkk.utils

import lt.markmerkk.ConfigPathProvider
import java.util.*

/**
 * @author mariusmerkevicius
 * @since 2016-11-07
 */
class ConfigSetSettingsImpl(
        private val configPathProvider: ConfigPathProvider
) : BaseSettings(), ConfigSetSettings {
    override var configSetName: String = ""

    override fun propertyPath(): String {
        val rootPath = configPathProvider.absolutePathWithMissingFolderCreate(
                configPathProvider.userHome() + "/.${configPathProvider.configDefault()}/"
        )
        return rootPath + PROPERTIES_PATH
    }

    override fun onLoad(properties: Properties) {
        configSetName = properties.getOrDefault(KEY, "").toString()
    }

    override fun onSave(properties: Properties) {
        properties.put(KEY, configSetName)
    }

    companion object {
        const val KEY = "config_set_name"
        const val PROPERTIES_PATH = "config_set.properties"
    }

}