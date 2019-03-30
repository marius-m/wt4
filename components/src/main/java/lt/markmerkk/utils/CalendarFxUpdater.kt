package lt.markmerkk.utils

import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Auto updater that kicks in automatically (updates calendar views)
 * Lifecycle: [onAttach], [onDetach]
 */
class CalendarFxUpdater(
        private val listener: Listener,
        private val waitScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subscription: Subscription? = null

    fun onAttach() {
        subscription = Observable.interval(30, 30, TimeUnit.SECONDS, waitScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    listener.onCurrentTimeUpdate(LocalTime.now())
                })
    }
    fun onDetach() {
        subscription?.unsubscribe()
    }

    //region Classes

    interface Listener {
        fun onCurrentTimeUpdate(currentTime: LocalTime)
    }

    //endregion

}