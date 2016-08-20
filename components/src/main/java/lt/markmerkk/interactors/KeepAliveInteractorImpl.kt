package lt.markmerkk.interactors

import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
class KeepAliveInteractorImpl(
        private val uiSCheduler: Scheduler,
        private val ioScheduler: Scheduler
) : KeepAliveInteractor {

    private var subscription: Subscription? = null
    private val listeners = mutableListOf<KeepAliveInteractor.Listener>()

    override fun register(listener: KeepAliveInteractor.Listener) {
        listeners.add(listener)
    }

    override fun unregister(listener: KeepAliveInteractor.Listener) {
        listeners.remove(listener)
    }

    override fun onAttach() {
        subscription = Observable.interval(0, 1, TimeUnit.MINUTES, ioScheduler)
                .observeOn(uiSCheduler)
                .subscribe({
                    listeners.forEach { it.update() }
                })
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeepAliveInteractorImpl::class.java)!!
    }

}