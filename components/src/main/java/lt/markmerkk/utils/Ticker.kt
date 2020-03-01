package lt.markmerkk.utils

import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventTickTock
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Auto updater that kicks in automatically
 * Lifecycle: [onAttach], [onDetach]
 */
class Ticker(
        private val eventBus: WTEventBus,
        private val waitScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subscription: Subscription? = null
    private var inFocus = AtomicBoolean(true)

    fun onAttach() {
        eventBus.register(this)
        subscription = Observable.interval(30, 30, TimeUnit.SECONDS, waitScheduler)
                .filter { inFocus.get() }
                .observeOn(uiScheduler)
                .subscribe({
                    eventBus.post(EventTickTock())
                }, { error ->
                    logger.warn("Error in auto update trigger", error)
                })
    }

    fun onDetach() {
        eventBus.unregister(this)
        subscription?.unsubscribe()
    }

    fun changeFocus(focused: Boolean) {
        inFocus.set(focused)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Ticker::class.java)!!
    }

}