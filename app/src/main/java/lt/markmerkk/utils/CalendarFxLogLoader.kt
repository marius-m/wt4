package lt.markmerkk.utils

import com.calendarfx.model.Entry
import com.calendarfx.model.Interval
import lt.markmerkk.entities.SimpleLog
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * Loads data for the day view
 * Lifecycle [onAttach], [onDetach]
 */
class CalendarFxLogLoader(
        private val view: View,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subscription: Subscription? = null

    fun onAttach() {}

    fun onDetach() {
        subscription?.unsubscribe()
    }

    fun load(logs: List<SimpleLog>) {
        subscription?.unsubscribe()
        subscription = Observable.just(logs)
                .subscribeOn(ioScheduler)
                .map {
                    it.map {
                        val zoneId = ZoneId.systemDefault()
                        val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.start), zoneId)
                        val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.end), zoneId)
                        val entry = Entry<SimpleLog>(
                                it.comment,
                                Interval(
                                        startDateTime.toLocalDate(),
                                        startDateTime.toLocalTime(),
                                        endDateTime.toLocalDate(),
                                        endDateTime.toLocalTime(),
                                        zoneId
                                )
                        )
                        entry.userObject = it
                        entry
                    }
                }
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.isEmpty()) {
                        view.onCalendarNoEntries()
                    } else {
                        view.onCalendarEntries(it)
                    }
                })
    }

    //region Classes

    interface View {
        fun onCalendarEntries(calendarEntries: List<Entry<SimpleLog>>)
        fun onCalendarNoEntries()
    }

    //endregion

}
