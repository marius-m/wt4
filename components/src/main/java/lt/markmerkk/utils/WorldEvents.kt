package lt.markmerkk.utils

/**
 * Created by mariusmerkevicius on 12/21/15.
 * Represents world events, that are needed for this class to function properly
 */
interface WorldEvents {
    /**
     * life-cycle event when starting class usage
     */
    fun onStart()

    /**
     * life-cycle event when stopping class usage
     */
    fun onStop()
}
