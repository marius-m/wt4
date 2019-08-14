package lt.markmerkk

import lt.markmerkk.events.EventsBusEvent

/**
 * Wraps event bus
 */
interface WTEventBus {
    fun register(any: Any)
    fun unregister(any: Any)
    fun post(event: EventsBusEvent)
}