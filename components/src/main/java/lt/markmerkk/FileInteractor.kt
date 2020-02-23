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
}