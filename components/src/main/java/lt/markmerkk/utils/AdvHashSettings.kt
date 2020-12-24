package lt.markmerkk.utils

import lt.markmerkk.Config
import java.io.File
import java.util.*

/**
 * Stores string data
 */
class AdvHashSettings(
        private val config: Config
) : BaseSettings(), HashSettings {

    val keyValues = mutableMapOf<String, String>()

    override operator fun get(key: String, defaultValue: String): String {
        if (!keyValues.containsKey(key))
            return defaultValue
        return keyValues[key] ?: defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return try {
            get(key, defaultValue.toString()).toLong()
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return try {
            get(key, defaultValue.toString()).toInt()
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return try {
            get(key, defaultValue.toString()).toBoolean()
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    override operator fun set(key: String, value: String) {
        keyValues.put(key, value)
    }

    override fun propertyPath(): String {
        return File(config.cfgPath, "${File.separator}${PROPERTIES_FILENAME}")
                .absolutePath
    }

    override fun onLoad(properties: Properties) {
        keyValues.clear()
        for (o in properties.keys)
            if (o is String) {
                val property = properties.getProperty(o)
                if (property.isNullOrEmpty()) continue
                val decodeBytes = Base64.decode(property, 0)
                val decodeValue = String(decodeBytes)
                keyValues.put(o, decodeValue)
            }
    }

    override fun onSave(properties: Properties) {
        for (s in keyValues.keys) {
            val value = keyValues[s]
            properties[s] = String(Base64.encode(value?.toByteArray(), 0)).trim { it <= ' ' }
        }
    }

    companion object {
        const val PROPERTIES_FILENAME = "usr.properties"
    }
}
