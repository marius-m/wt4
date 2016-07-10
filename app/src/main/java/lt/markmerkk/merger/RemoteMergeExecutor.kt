package lt.markmerkk.merger

import lt.markmerkk.storage2.SimpleLog
import net.rcarz.jiraclient.WorkLog

interface RemoteMergeExecutor {
    /**
     * Creates a new local log entry
     */
    fun createLog(simpleLog: SimpleLog)

    /**
     * Updates an old local entity entry
     */
    fun updateLog(simpleLog: SimpleLog)

    /**
     * Pulls remote entity equivalent from local storage if available.
     * Will return null if no such entity exists
     */
    fun localEntityFromRemote(remoteWorklog: WorkLog): SimpleLog?

}