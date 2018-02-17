package lt.markmerkk.ui.day

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
class DayViewLoader(
        private val view: DayViewLoader.View,
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
        subscription = Observable.from(logs)
                .subscribeOn(ioScheduler)
                .map {
                    val zoneId = ZoneId.systemDefault()
                    val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.start), zoneId)
                    val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.end), zoneId)
                    Entry<String>(
                            it.comment,
                            Interval(
                                    startDateTime.toLocalDate(),
                                    startDateTime.toLocalTime(),
                                    endDateTime.toLocalDate(),
                                    endDateTime.toLocalTime(),
                                    zoneId
                            )
                    )
                }
                .toList()
                .observeOn(uiScheduler)
                .subscribe({
                    view.onCalendarEntries(it)
                })
    }

    //region Classes

    interface View {
        fun onCalendarEntries(calendarEntries: List<Entry<String>>)
    }

    //endregion

}

