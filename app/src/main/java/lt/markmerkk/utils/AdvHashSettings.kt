package lt.markmerkk.utils

import lt.markmerkk.Main
import java.util.*

/**
 * Created by mariusmerkevicius on 11/24/15.
 * Stores string data
 */
class AdvHashSettings : BaseSettings(), HashSettings {

    val keyValues = mutableMapOf<String, String>()

    override operator fun get(key: String): String? {
        if (!keyValues.containsKey(key))
            return null
        return keyValues[key]
    }

    override operator fun set(key: String, value: String) {
        keyValues.put(key, value)
    }

    override fun propertyPath(): String {
        return Main.CFG_PATH + PROPERTIES_PATH
    }

    override fun onLoad(properties: Properties) {
        keyValues.clear()
        for (o in properties.keys)
            if (o is String) {
                val property = properties.getProperty(o)
                if (Utils.isEmpty(property)) continue
                val decodeBytes = Base64.decode(property, 0)
                val decodeValue = String(decodeBytes)
                keyValues.put(o, decodeValue)
            }
    }

    override fun onSave(properties: Properties) {
        for (s in keyValues.keys) {
            val value = keyValues[s]
            properties.put(s, String(Base64.encode(value?.toByteArray(), 0)).trim { it <= ' ' })
        }
    }

    companion object {
        val PROPERTIES_PATH = "usr.properties"
    }
}
