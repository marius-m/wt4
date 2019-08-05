package lt.markmerkk.interactors

/**
 * Responsible for controlling setRunning clock events
 */
@Deprecated("Unused")
interface ClockRunBridge {
    /**
     * Trigger run function
     */
    fun setRunning(isRunning: Boolean)

    /**
     * Logs message
     */
    fun log(ticket: String, message: String)
}