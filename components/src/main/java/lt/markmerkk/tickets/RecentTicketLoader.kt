package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketStorage
import lt.markmerkk.entities.TicketUseHistory
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

class RecentTicketLoader(
        private val listener: Listener,
        private val ticketStorage: TicketStorage,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var fetchSub: Subscription? = null
    private val originalTickets: MutableList<TicketUseHistory> = mutableListOf()

    private var rawInputFilter: String = ""

    fun onAttach() {}

    fun onDetach() {
        fetchSub?.unsubscribe()
    }

    fun fetch() {
        fetchSub?.unsubscribe()
        fetchSub = ticketStorage.fetchRecentTickets(20)
                .flatMapObservable { Observable.from(it) }
                .flatMapSingle { ticketUseHistory ->
                    ticketStorage.findTicketsByCode(ticketUseHistory.code.code)
                            .map { ticket -> ticket.firstOrNull() }
                            .map { ticket ->
                                ticketUseHistory.appendDescription(
                                        description = ticket?.description ?: ""
                                )
                            }
                }
                .toList()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({ tickets ->
                    val boundTickets = bindTickets(tickets)
                    val filteredTickets = filterLoadedTickets(
                        tickets = boundTickets,
                        rawInput = rawInputFilter,
                    )
                    listener.onRecentTickets(filteredTickets)
                }, {
                    logger.warn("Error fetch tickets", it)
                })
    }

    fun filterLoadedTickets(
        publishResults: Boolean = false,
        tickets: List<TicketUseHistory> = this.originalTickets,
        rawInput: String,
    ): List<TicketUseHistory> {
        this.rawInputFilter = rawInput
        val sortedRecentTickets = tickets
            .filter { ticket ->
                ticket.code.codeProject.contains(rawInput, ignoreCase = true)
                    || ticket.code.codeNumber.contains(rawInput)
                    || ticket.description.contains(rawInput, ignoreCase = true)
            }
            .sortedBy { it.lastUsed }
            .reversed()
        if (publishResults) {
            listener.onRecentTickets(sortedRecentTickets)
        }
        return sortedRecentTickets
    }

    fun bindTickets(tickets: List<TicketUseHistory>): List<TicketUseHistory> {
        val sortedRecentTickets = tickets
            .sortedBy { it.lastUsed }
            .reversed()
        this.originalTickets.clear()
        this.originalTickets.addAll(sortedRecentTickets)
        return sortedRecentTickets
    }

    interface Listener {
        fun onRecentTickets(tickets: List<TicketUseHistory>)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }
}