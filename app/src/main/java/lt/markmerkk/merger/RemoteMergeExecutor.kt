package lt.markmerkk.merger

import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.WorkLog

interface RemoteMergeExecutor<LocalType, in RemoteType> {
    /**
     * Creates a new local log entry
     */
    fun createLog(simpleLog: LocalType)

    /**
     * Updates an old local entity entry
     */
    fun updateLog(simpleLog: LocalType)

    /**
     * Pulls remote entity equivalent from local storage if available.
     * Will return null if no such entity exists
     */
    fun localEntityFromRemote(remoteWorklog: RemoteType): LocalType?

    /**
     * Recreates a local log with new worklog data
     */
    fun recreateLog(oldLocalLog: LocalType, remoteWorklog: RemoteType)

    /**
     * Marks log as error
     */
    fun markAsError(oldLocalLog: LocalType, error: Throwable)

}