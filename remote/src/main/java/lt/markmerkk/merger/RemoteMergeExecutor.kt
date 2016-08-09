package lt.markmerkk.merger

interface RemoteMergeExecutor<LocalType, in RemoteType> {
    /**
     * Creates a new entry
     */
    fun create(entry: LocalType)

    /**
     * Updates an old entry
     */
    fun update(entry: LocalType)

    /**
     * Pulls remote entity equivalent from local storage if available.
     * Will return null if no such entity exists
     */
    fun localEntityFromRemote(remoteEntry: RemoteType): LocalType?

    /**
     * Recreates an entry with new remote data
     */
    fun recreate(oldLocalEntry: LocalType, remoteEntry: RemoteType)

    /**
     * Marks entry as error
     */
    fun markAsError(oldLocalEntry: LocalType, error: Throwable)

}