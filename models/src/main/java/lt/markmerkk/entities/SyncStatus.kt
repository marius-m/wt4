package lt.markmerkk.entities

import lt.markmerkk.entities.Log.Companion.isRemoteError

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
         * Exposes [SyncStatus] from [Log]
         */
        @JvmStatic fun exposeStatus(log: Log): SyncStatus {
            return when {
                log.remoteData.isRemoteError() -> ERROR
                log.isRemote -> IN_SYNC
                !log.isRemote -> WAITING_FOR_SYNC
                else -> INVALID
            }
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