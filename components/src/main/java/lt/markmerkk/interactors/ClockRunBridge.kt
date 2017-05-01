package lt.markmerkk.interactors

/**
 * Responsible for controlling setRunning clock events
 */
interface ClockRunBridge {
    /**
     * Trigger run function
     */
    fun setRunning(isRunning: Boolean)

    /**
     * Logs message
     */
    fun log(message: String)
}