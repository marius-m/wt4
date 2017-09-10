package lt.markmerkk.interactors

import lt.markmerkk.LogStorage
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * @since 2016-08-13
 */
class KeepAliveGASessionImpl(
        private val logStorage: LogStorage,
        private val tracker: ITracker,
        private val ioScheduler: Scheduler
) : KeepAliveGASession {

    private var subscription: Subscription? = null

    override fun onAttach() {
        subscription = Observable.interval(MINUTE_DELAY, MINUTE_DELAY, TimeUnit.MINUTES, ioScheduler)
                .subscribe({ tracker.sendView(logStorage.displayType.name) })
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    companion object {
        const val MINUTE_DELAY = 10L
        private val logger = LoggerFactory.getLogger(KeepAliveGASessionImpl::class.java)!!
    }
}