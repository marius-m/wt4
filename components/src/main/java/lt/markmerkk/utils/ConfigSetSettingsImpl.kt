package lt.markmerkk.utils

import lt.markmerkk.ConfigPathProvider
import org.slf4j.LoggerFactory
import java.util.*

class ConfigSetSettingsImpl(
        private val configPathProvider: ConfigPathProvider
) : BaseSettings(), ConfigSetSettings {

    private var configSetName: String = ""
        set(value) {
            field = sanitizeConfigName(value)
        }
    private var configs: List<String> = emptyList()

    override fun changeActiveConfig(configSelection: String) {
        configSetName = configSelection
    }

    override fun configs(): List<String> {
        return configs
                .plus(DEFAULT_ROOT_CONFIG_NAME)
    }

    override fun currentConfig(): String {
        return configSetName
    }

    override fun currentConfigOrDefault(): String {
        if (configSetName.isEmpty()) {
            return DEFAULT_ROOT_CONFIG_NAME
        }
        return configSetName
    }

    override fun propertyPath(): String {
        val rootPath = configPathProvider.absolutePathWithMissingFolderCreate(
                configPathProvider.userHome() + "/.${configPathProvider.configDefault()}/"
        )
        return rootPath + PROPERTIES_PATH
    }

    override fun onLoad(properties: Properties) {
        configs = configsFromProperty(properties.getOrDefault(KEY_CONFIGS, "").toString())
        configSetName = properties.getOrDefault(KEY_CONFIG_NAME, "").toString()
    }

    override fun onSave(properties: Properties) {
        properties.put(KEY_CONFIG_NAME, configSetName)
        properties.put(KEY_CONFIGS, configsToProperty(configs))
    }

    //region Convenience

    /**
     * Sanitizes input value to be valid for a validation name
     * If no such config exist, will create a new one in the [configs]
     *
     * Note: "default" value will be reserved and interpreted as an empty string.
     */
    fun sanitizeConfigName(inputValue: String): String {
        if (inputValue.isEmpty()) return ""
        if (inputValue == DEFAULT_ROOT_CONFIG_NAME) return ""
        if (!configs.contains(inputValue)) {
            configs += inputValue
        }
        return inputValue.replace(",", "").trim()
    }

    /**
     * Transforms a list of configs from property
     */
    fun configsFromProperty(property: String): List<String> {
        if (property.isEmpty()) return emptyList()
        return property.split(",")
                .filter { it != "\n" }
                .filter { it != "\r\n" }
                .map(String::trim)
                .filter { !it.isEmpty() }
                .toList()
    }

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
                            .trim()
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
        const val DEFAULT_ROOT_CONFIG_NAME = "default"
        const val PROPERTIES_PATH = "config_set.properties"
        val logger = LoggerFactory.getLogger(ConfigSetSettingsImpl::class.java)!!
    }

}