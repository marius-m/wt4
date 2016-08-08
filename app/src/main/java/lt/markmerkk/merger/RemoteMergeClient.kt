package lt.markmerkk.merger

import lt.markmerkk.storage2.SimpleLog
import net.rcarz.jiraclient.WorkLog

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
interface RemoteMergeClient {
    /**
     * Uploads a local log.
     * Returns an out log if successfully uploaded. Otherwise will return null
     */
    fun uploadLog(simpleLog: SimpleLog): WorkLog?
}