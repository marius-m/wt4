package lt.markmerkk.tickets

import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Responsible for loading tickets
 */
class TicketLoader(
        private val listener: Listener,
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val ticketsNetworkRepo: TicketsNetworkRepo,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private val inputFilterSubject = BehaviorSubject.create<String>("")
    private var inputFilterSubscription: Subscription? = null
    private var networkSubscription: Subscription? = null
    private var dbSubscription: Subscription? = null

    private var tickets: List<Ticket> = emptyList()
    private var inputFilter: String = ""

    fun onAttach() {
        inputFilterSubscription = inputFilterSubject
                .throttleLast(FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS, ioScheduler)
                .flatMap { Observable.just(filter(tickets, it)) }
                .observeOn(uiScheduler)
                .subscribe { listener.onTicketsAvailable(it) }
    }

    fun onDetach() {
        dbSubscription?.unsubscribe()
        networkSubscription?.unsubscribe()
        inputFilterSubscription?.unsubscribe()
    }

    fun fetchTickets(
            forceRefresh: Boolean = false
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
            return
        }
        logger.info("Refreshing tickets")
        networkSubscription = ticketsNetworkRepo.searchRemoteTicketsAndCache(timeProvider.now())
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { listener.onLoadStart() }
                .doAfterTerminate { listener.onLoadFinish() }
                .subscribe({
                    userSettings.ticketLastUpdate = now.millis
                    if (it.isNotEmpty()) {
                        listener.onNewTickets(it)
                        loadTickets(inputFilter)
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
        dbSubscription = ticketsDatabaseRepo.loadTickets()
                .map { filter(it, searchInput = inputFilter) }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.isNotEmpty()) {
                        listener.onTicketsAvailable(it)
                        tickets = it
                    } else {
                        listener.onNoTickets()
                        tickets = emptyList()
                    }
                }, {
                    listener.onError(it)
                    tickets = emptyList()
                })
    }

    fun applyFilter(inputFilter: String) {
        this.inputFilter = inputFilter
        this.inputFilterSubject.onNext(inputFilter)
    }

    interface Listener {
        fun onLoadStart()
        fun onLoadFinish()
        fun onNewTickets(tickets: List<Ticket>)
        fun onTicketsAvailable(tickets: List<Ticket>)
        fun onNoTickets()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
        const val TICKET_TIMEOUT_MINUTES = 30
        const val FILTER_MIN_INPUT = 1
        const val FILTER_FUZZY_SCORE = 50
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

        fun filter(inputTickets: List<Ticket>, searchInput: String): List<Ticket> {
            if (searchInput.length <= FILTER_MIN_INPUT) {
                return inputTickets
            }
            val ticketDescriptions = inputTickets
                    .map { "${it.code.code} ${it.description}" }
            val topDescriptions = FuzzySearch.extractTop(searchInput, ticketDescriptions, 100)
                    .filter { it.score > FILTER_FUZZY_SCORE }
                    .map { it.string }
            return inputTickets
                    .filter { topDescriptions.contains("${it.code.code} ${it.description}") }
        }
    }

}