package lt.markmerkk.utils

import org.slf4j.LoggerFactory
import java.io.*
import java.util.*

/**
 * Created by mariusmerkevicius on 11/24/15.
 * Uses a persistent storage to save user info
 */
abstract class BaseSettings {
    //public static final String PROPERTIES_FILE = "usr.properties";

    abstract fun propertyPath(): String

    /**
     * Loads properties from file system
     * @param properties input file to load properties into
     */
    abstract fun onLoad(properties: Properties)

    /**
     * Saves properties into file system
     * @param properties input properties
     */
    abstract fun onSave(properties: Properties)

    //region Core

    /**
     * Core method to load properties from local storage
     */
    fun load() {
        synchronized(this) {
            var inputStream: InputStream? = null
            try {
                inputStream = FileInputStream(propertyPath())
                val props = Properties()
                props.load(inputStream)
                onLoad(props)
                logger.info("Success loading settings from ${propertyPath()}!")
            } catch (e: FileNotFoundException) {
                logger.error("No default settings file found! " + e.message)
            } catch (e: IOException) {
                logger.error("Error opening settings file!" + e.message)
            } finally {
                inputStream?.close()
            }
        }
    }

    /**
     * Core method to save properties to local storage
     */
    fun save() {
        synchronized(this) {
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(propertyPath())
                val props = Properties()
                onSave(props)
                props.store(outputStream, null)
                logger.info("Success saving settings to ${propertyPath()}")
            } catch (e: FileNotFoundException) {
                logger.error("No default settings file found! " + e.message)
            } catch (e: IOException) {
                logger.error("Error opening settings file!" + e.message)
            } finally {
                outputStream?.close()
            }
        }
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(BaseSettings::class.java)!!
    }

}
