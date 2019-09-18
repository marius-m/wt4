package lt.markmerkk.utils

/**
 * Does first internal check if account it available for sync
 */
interface AccountAvailablility {
    fun host(): String
    fun isAccountReadyForSync(): Boolean
}