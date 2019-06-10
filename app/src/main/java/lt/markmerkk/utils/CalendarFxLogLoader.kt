package lt.markmerkk.utils

import com.calendarfx.model.Entry
import com.calendarfx.model.Interval
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SyncStatus
import rx.Observable
import rx.Scheduler
import rx.Subscription

/**
 * Loads data for the day view
 * Lifecycle [onAttach], [onDetach]
 */
class CalendarFxLogLoader(
        private val view: View,
        private val timeProvider: TimeProvider,
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
        subscription = Observable.defer { Observable.just(logs) }
                .subscribeOn(ioScheduler)
                .map { logEntry ->
                    logEntry.map {
                        val startDateTime = timeProvider.roundDateTimeJava8(it.start)
                        val endDateTime = timeProvider.roundDateTimeJava8(it.end)
                        val entry = Entry<SimpleLog>(
                                LogUtils.formatLogToText(it),
                                Interval(
                                        startDateTime.toLocalDate(),
                                        startDateTime.toLocalTime(),
                                        endDateTime.toLocalDate(),
                                        endDateTime.toLocalTime(),
                                        timeProvider.zoneId
                                )
                        )
                        entry.userObject = it
                        entry
                    }
                }
                .observeOn(uiScheduler)
                .subscribe {
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
                }
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

