package lt.markmerkk.mvp

import rx.Observable

/**
 * Responsible for loading logs from file
 */
interface LogLoader {
    /**
     * Tries to load last lines from the file and returns as a string.
     * If no such file or error reading will return empty
     */
    fun loadLastLogs(fileName: String, linesToLoad: Int): String

}