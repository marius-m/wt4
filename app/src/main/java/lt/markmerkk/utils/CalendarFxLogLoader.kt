package lt.markmerkk.utils

import com.calendarfx.model.Entry
import com.calendarfx.model.Interval
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SyncStatus
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
                                .withSecond(0)
                                .withNano(0)
                        val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.end), zoneId)
                                .withSecond(0)
                                .withNano(0)
                        val entry = Entry<SimpleLog>(
                                LogUtils.formatLogToText(it),
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
                        view.onCalendarEntries(
                                allEntries = it,
                                entriesInSync = it.filter { SyncStatus.exposeStatus(it.userObject) == SyncStatus.IN_SYNC },
                                entriesWaitingForSync = it.filter { SyncStatus.exposeStatus(it.userObject) == SyncStatus.WAITING_FOR_SYNC },
                                entriesInError = it.filter { SyncStatus.exposeStatus(it.userObject) == SyncStatus.ERROR }
                        )
                    }
                })
    }

    //region Classes

    interface View {
        fun onCalendarEntries(
                allEntries: List<Entry<SimpleLog>>,
                entriesInSync: List<Entry<SimpleLog>>,
                entriesWaitingForSync: List<Entry<SimpleLog>>,
                entriesInError: List<Entry<SimpleLog>>
        )
        fun onCalendarNoEntries()
    }

    //endregion

}

