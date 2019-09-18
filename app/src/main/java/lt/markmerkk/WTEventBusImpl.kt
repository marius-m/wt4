package lt.markmerkk

import com.google.common.eventbus.EventBus
import lt.markmerkk.events.EventsBusEvent

class WTEventBusImpl(
        private val eventBus: EventBus
): WTEventBus {
    override fun register(any: Any) {
        eventBus.register(any)
    }

    override fun unregister(any: Any) {
        eventBus.unregister(any)
    }

    override fun post(event: EventsBusEvent) {
        eventBus.post(event)
    }
}