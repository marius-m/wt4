package lt.markmerkk.utils

/**
 * Does first internal check if account it available for sync
 */
interface AccountAvailablilityInteractor {
    fun host(): String
    fun isAccountReadyForSync(): Boolean
}