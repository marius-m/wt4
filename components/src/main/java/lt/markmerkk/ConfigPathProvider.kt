package lt.markmerkk

import java.io.File

interface ConfigPathProvider {
    /**
     * Path for user home directory
     */
    fun userHome(): String

    /**
     * Default config middle path. For ex.: '.middle_path/'
     */
    fun configDefault(): String

    /**
     * Full app path
     */
    fun fullAppDir(): File

}