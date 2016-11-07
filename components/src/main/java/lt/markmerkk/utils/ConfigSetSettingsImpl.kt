package lt.markmerkk.utils

import java.util.*

/**
 * @author mariusmerkevicius
 * @since 2016-11-07
 */
class ConfigSetSettingsImpl : BaseSettings(), ConfigSetSettings {
    override var configSetName: String = ""

    override fun propertyPath(): String = "config_set.properties"

    override fun onLoad(properties: Properties) {
        configSetName = properties.getOrDefault(KEY, "").toString()
    }

    override fun onSave(properties: Properties) {
        properties.put(KEY, configSetName)
    }

    companion object {
        const val KEY = "config_set_name"
    }

}