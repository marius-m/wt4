package lt.markmerkk.utils

import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by mariusmerkevicius on 11/24/15.
 * First time launch settings
 */
class FirstSettings : BaseSettings() {
    var isFirst = true
        private set

    override fun propertyPath(): String {
        return PROPERTIES_FILE
    }

    override fun onLoad(properties: Properties) {
        val firstProperty = properties.getProperty("first", "true")
        isFirst = "false" != firstProperty
    }

    override fun onSave(properties: Properties) {
        properties.put("first", "false")
    }

    companion object {
        var logger = LoggerFactory.getLogger(FirstSettings::class.java)
        val PROPERTIES_FILE = "first.properties"
    }

}
