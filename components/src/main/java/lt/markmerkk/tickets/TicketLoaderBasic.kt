package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Single
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * Uses basic search funct to fetch tickets
 */
class TicketLoaderBasic(
        private val listener: Listener,
        private val ticketStorage: TicketStorage,
        private val ticketApi: TicketApi,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var inputFilterSubscription: Subscription? = null
    private var dbSubscription: Subscription? = null

    fun onAttach() {}

    fun onDetach() {
//        dbSubsCodes?.unsubscribe()
        dbSubscription?.unsubscribe()
//        networkSubscription?.unsubscribe()
        inputFilterSubscription?.unsubscribe()
    }

    fun loadTickets(
            inputFilter: String = ""
    ) {
        logger.info("Loading tickets from database with filter: $inputFilter")
        dbSubscription?.unsubscribe()
        dbSubscription = loadTicketsAsStream(inputFilter)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.isNotEmpty()) {
                        listener.onFoundTickets(it)
                    } else {
                        listener.onNoTickets()
                    }
                }, {
                    listener.onError(it)
                })
    }

    fun loadTicketsAsStream(
            inputFilter: String
    ): Single<List<Ticket>> {
        return ticketStorage
                .loadTicketsWithEnabledStatus(inputFilter)
    }

    fun changeFilterStream(filterChange: Observable<String>) {
        inputFilterSubscription = filterChange
                .throttleLast(FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS, ioScheduler)
                .flatMapSingle { loadTicketsAsStream(it) }
                .observeOn(uiScheduler)
                .subscribe { listener.onFoundTickets(it) }
    }

    interface Listener {
        fun onLoadStart()
        fun onLoadFinish()
        fun onFoundTickets(tickets: List<Ticket>)
        fun onNoTickets()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)

        const val TICKET_TIMEOUT_MINUTES = 30
        const val FILTER_MIN_INPUT = 1
        const val FILTER_FUZZY_SCORE = 20
        const val FILTER_INPUT_THROTTLE_MILLIS = 500L
    }
}