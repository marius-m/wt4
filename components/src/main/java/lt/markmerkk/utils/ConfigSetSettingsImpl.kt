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
    val someList = listOf<String>("test1", "test2")

    override fun propertyPath(): String {
        val rootPath = configPathProvider.absolutePathWithMissingFolderCreate(
                configPathProvider.userHome() + "/.${configPathProvider.configDefault()}/"
        )
        return rootPath + PROPERTIES_PATH
    }

    override fun onLoad(properties: Properties) {
        configSetName = properties.getOrDefault(KEY_CONFIG_NAME, "").toString()
    }

    override fun onSave(properties: Properties) {
        properties.put(KEY_CONFIG_NAME, configSetName)
        properties.put(KEY_CONFIGS, configsToProperty(someList))
    }

    //region Convenience

    /**
     * Transforms list of configs to property to be saved.
     * Will take care of invalid items.
     */
    fun configsToProperty(configs: List<String>): String {
        val sb = StringBuilder()
        for (config in configs) {
            if (config.isNullOrEmpty()) continue
            sb.append(
                    config.toString()
                            .replace(",", "")
            )
            sb.append(",")
        }
        if (sb.length > 0) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    //endregion

    companion object {
        const val KEY_CONFIG_NAME = "config_set_name"
        const val KEY_CONFIGS = "configs"
        const val PROPERTIES_PATH = "config_set.properties"
    }

}