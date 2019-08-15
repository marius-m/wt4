package lt.markmerkk.tickets

import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Single
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * Responsible for loading tickets
 */
class TicketLoader(
        private val listener: Listener,
        private val ticketStorage: TicketStorage,
        private val ticketApi: TicketApi,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var inputFilterSubscription: Subscription? = null
    private var networkSubscription: Subscription? = null
    private var dbSubscription: Subscription? = null

    private var inputFilter: String = ""

    fun onAttach() { }

    fun onDetach() {
        dbSubscription?.unsubscribe()
        networkSubscription?.unsubscribe()
        inputFilterSubscription?.unsubscribe()
    }

    fun fetchTickets(
            forceRefresh: Boolean = false,
            inputFilter: String = ""
    ) {
        networkSubscription?.unsubscribe()
        val now = timeProvider.now()
        val isFreshEnough = isTicketFreshEnough(
                lastTimeout = DateTime(userSettings.ticketLastUpdate),
                timeoutInMinutes = TICKET_TIMEOUT_MINUTES,
                now = now
        )
        if (isFreshEnough && !forceRefresh) {
            logger.info("Ignoring ticket search, as tickets are fresh enough")
            loadTickets(inputFilter)
            return
        }
        logger.info("Refreshing tickets")
        networkSubscription = ticketApi.searchRemoteTicketsAndCache(timeProvider.now())
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { listener.onLoadStart() }
                .doAfterTerminate { listener.onLoadFinish() }
                .flatMap { loadTicketsAsStream(inputFilter) }
                .subscribe({
                    userSettings.ticketLastUpdate = now.millis
                    if (it.isNotEmpty()) {
                        listener.onFoundTickets(it)
                    } else {
                        listener.onNoTickets()
                    }
                }, {
                    listener.onError(it)
                })
    }

    fun stopFetch() {
        networkSubscription?.unsubscribe()
    }

    fun loadTickets(inputFilter: String = "") {
        logger.info("Loading tickets from database with filter: $inputFilter")
        this.inputFilter = inputFilter
        dbSubscription?.unsubscribe()
        dbSubscription = ticketStorage.loadTickets()
                .map { filter(it, searchInput = inputFilter) }
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

    fun loadTicketsAsStream(inputFilter: String = ""): Single<List<TicketScore>> {
        return ticketStorage.loadTickets()
                .map { filter(it, searchInput = inputFilter) }
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
        fun onFoundTickets(tickets: List<TicketScore>)
        fun onNoTickets()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
        const val TICKET_TIMEOUT_MINUTES = 30
        const val FILTER_MIN_INPUT = 1
        const val FILTER_FUZZY_SCORE = 20
        const val FILTER_INPUT_THROTTLE_MILLIS = 500L

        /**
         * @return unique project codes in tickets
         */
        fun filterProjectCodes(tickets: List<Ticket>): List<String> {
            return tickets
                    .map { it.code.codeProject }
                    .toSet()
                    .toList()
        }

        /**
         * Checks if timeout has expired to fetch new tickets from
         * the network
         */
        fun isTicketFreshEnough(
                lastTimeout: DateTime,
                timeoutInMinutes: Int,
                now: DateTime
        ): Boolean {
            return DateTime(lastTimeout).plusMinutes(timeoutInMinutes)
                    .isAfter(now)
        }

        fun filter(inputTickets: List<Ticket>, searchInput: String): List<TicketScore> {
            if (searchInput.length <= FILTER_MIN_INPUT) {
                return inputTickets.map { TicketScore(it, 0) }
            }
            val ticketDescriptions = inputTickets
                    .map { it.description }
            return FuzzySearch.extractTop(searchInput, ticketDescriptions, 100)
                    .filter { it.score > FILTER_FUZZY_SCORE }
                    .map { TicketScore(inputTickets[it.index], it.score) }
        }
    }

    data class TicketScore(
            val ticket: Ticket,
            val filterScore: Int
    )

}

fun Ticket.withScore(score: Int): TicketLoader.TicketScore = TicketLoader.TicketScore(this, score)