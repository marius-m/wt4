package lt.markmerkk.interactors

import rx.Observable

/**
 * Responsible for loading logs from file
 */
@Deprecated("Rm")
interface LogLoader {
    /**
     * Tries to load last lines from the file and returns as a string.
     * If no such file or error reading will return empty
     */
    fun loadLastLogs(fileName: String, linesToLoad: Int): String

}