package lt.markmerkk

import com.google.common.eventbus.EventBus

class WTEventBusImpl(
        private val eventBus: EventBus
): WTEventBus {
    override fun register(any: Any) {
        eventBus.register(any)
    }

    override fun unregister(any: Any) {
        eventBus.unregister(any)
    }

    override fun post(any: Any) {
        eventBus.post(any)
    }
}