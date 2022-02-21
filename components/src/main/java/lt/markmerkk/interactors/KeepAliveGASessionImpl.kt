package lt.markmerkk.interactors

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit

class KeepAliveGASessionImpl(
    private val activeDisplayRepository: ActiveDisplayRepository,
    private val tracker: ITracker,
    private val waitScheduler: Scheduler
) : KeepAliveGASession {

    private var subscription: Subscription? = null

    override fun onAttach() {
        subscription = Observable.interval(MINUTE_DELAY, TimeUnit.MINUTES, waitScheduler)
                .subscribe({
                    tracker.sendView(activeDisplayRepository.displayType.name)
                }, { error ->
                    logger.warn("Error sending ping", error)
                })
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    companion object {
        const val MINUTE_DELAY = 10L
        private val logger = LoggerFactory.getLogger(KeepAliveGASessionImpl::class.java)!!
    }
}