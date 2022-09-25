package lt.markmerkk

import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

/**
 * @author mariusmerkevicius
 * @since 2017-09-16
 */
class StringsImpl : Strings {

    lateinit var strings: Map<String, String>

    init {
        try {
            val translationsAsProperties = Properties()
            val resourceAsStream = javaClass.getResourceAsStream("/translations_en.properties")
            translationsAsProperties.load(resourceAsStream)
            val translations = mutableMapOf<String, String>()
            translationsAsProperties.forEach { key, value -> translations.put(key as String, value as String) }
            strings = translations.toMap()
        } catch (e: IOException) {
            logger.error("[ERROR] No translations were initialized!", e)
        }
    }

    override fun getString(stringKey: String): String {
        if (!strings.containsKey(stringKey)) {
            logger.warn("Cannot find translation key `$stringKey`!")
        }
        return strings.getOrDefault(stringKey, stringKey)
    }

    companion object {
        val logger = LoggerFactory.getLogger(Tags.TRANSLATE)!!
    }

}