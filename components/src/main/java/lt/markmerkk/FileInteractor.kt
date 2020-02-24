package lt.markmerkk

import java.io.File

/**
 * General layer for file system interactor
 */
interface FileInteractor {
    /**
     * Finds a directory to save to
     */
    fun saveDirectory(): File?

    /**
     * Selects a file to load
     */
    fun saveFile(): File?

    /**
     * Selects a file to load
     */
    fun loadFile(): File?
}