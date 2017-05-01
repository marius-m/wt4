package lt.markmerkk.interactors

/**
 * Responsible for controlling setRunning clock events
 */
interface ClockRunBridge {
    fun setRunning(isRunning: Boolean)
}