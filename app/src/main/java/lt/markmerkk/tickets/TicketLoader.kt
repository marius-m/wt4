package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription

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

    private var networkSubscription: Subscription? = null
    private var dbSubscription: Subscription? = null
    private var tickets: List<Ticket> = emptyList()

    fun onAttach() {}
    fun onDetach() {
        dbSubscription?.unsubscribe()
        networkSubscription?.unsubscribe()
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
                .subscribe({
                    if (it.isNotEmpty()) {
                        listener.onNewTickets(it)
                        loadTickets()
                    }
                }, {
                    listener.onError(it)
                })
    }

    fun loadTickets() {
        logger.info("Loading tickets from database")
        dbSubscription?.unsubscribe()
        dbSubscription = ticketsDatabaseRepo.loadTickets()
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

    fun search(searchInput: String) {
        if (searchInput.length <= 1) {
            listener.onTicketsAvailable(tickets)
            return
        }
        val ticketDescriptions = tickets.map { it.description }
        val topDescriptions = FuzzySearch.extractTop(searchInput, ticketDescriptions, 100)
                .filter { it.score > 50 }
                .map { it.string }
        val ticketsWithSimilarCode = tickets
                .filter {
                    it.code.codeProject.contains(searchInput, ignoreCase = true)
                            || it.code.codeNumber.contains(searchInput)
                            || it.code.code.contains(searchInput, ignoreCase = true)
                }
        val ticketsWithFilterOnDescriptions = tickets.filter { topDescriptions.contains(it.description) }
        val ticketResult = ticketsWithSimilarCode
                .plus(ticketsWithFilterOnDescriptions)
        listener.onTicketsAvailable(ticketResult)
    }

    interface Listener {
        fun onNewTickets(tickets: List<Ticket>)
        fun onTicketsAvailable(tickets: List<Ticket>)
        fun onNoTickets()
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
        const val TICKET_TIMEOUT_MINUTES = 30

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
    }

}