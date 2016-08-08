package lt.markmerkk.merger

import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
interface RemoteMergeClient {
    /**
     * Uploads a local log.
     * Returns an out log if successfully uploaded.
     * Will throw an error due to upload problem
     */
    @Throws(JiraException::class)
    fun uploadLog(simpleLog: SimpleLog): WorkLog
}