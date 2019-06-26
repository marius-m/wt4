package lt.markmerkk

/**
 * Provides a bus that sends events to global listeners
 */
interface WTEventBus {
    /**
     * Registers a listener to listen for an event
     */
    fun register(any: Any)

    /**
     * Unregisters from the event listening
     */
    fun unregister(any: Any)

    /**
     * Sends an event to any listener
     */
    fun post(any: Any)
}