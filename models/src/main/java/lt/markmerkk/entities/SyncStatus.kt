package lt.markmerkk.entities

/**
 * Defines sync status for the entries
 */
enum class SyncStatus {
    /**
     * Entry status is INVALID and should be handled accordingly.
     */
    INVALID,

    /**
     * Entry is already in sync
     */
    IN_SYNC,

    /**
     * Entry has some errors and cannot be synced
     */
    ERROR,

    /**
     * Still waiting for the sync
     */
    WAITING_FOR_SYNC,
    ;

    companion object {
        /**
         * Exposes [SyncStatus] from [SimpleLog]
         */
        @JvmStatic fun exposeStatus(simpleLog: SimpleLog): SyncStatus {
            if (simpleLog.isDirty) {
                return WAITING_FOR_SYNC
            }
            if (simpleLog.isError) {
                return ERROR
            }
            if (!simpleLog.canEdit()) {
                return IN_SYNC
            }
            return INVALID
        }

        /**
         * Converts [SyncStatus] to its state representation
         */
        @JvmStatic fun toStatusImageUrl(syncStatus: SyncStatus): String {
            when (syncStatus) {
                IN_SYNC -> return "/green.png"
                WAITING_FOR_SYNC -> return "/yellow.png"
                ERROR -> return "/red.png"
                INVALID -> return "/gray.png"
            }
            throw IllegalStateException("Cannot handle $syncStatus")
        }
    }

}