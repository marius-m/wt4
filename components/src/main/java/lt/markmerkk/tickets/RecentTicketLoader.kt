package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketStorage
import lt.markmerkk.entities.Ticket
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

    fun onAttach() {}

    fun onDetach() {
        fetchSub?.unsubscribe()
    }

    fun fetch() {
        fetchSub?.unsubscribe()
        fetchSub = ticketStorage.fetchRecentTickets(6)
                .flatMapObservable { Observable.from(it) }
                .flatMapSingle { ticketUseHistory ->
                    ticketStorage.findTicketsByCode(ticketUseHistory.code.code)
                            .map { ticket -> ticket.firstOrNull() }
                            .map {
                                ticketUseHistory.appendDescription(
                                        description = it?.description ?: ""
                                )
                            }
                }
                .toList()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({ tickets ->
                    val sortedRecentTickets = tickets
                            .sortedBy { it.lastUsed }
                            .reversed()
                    listener.onRecentTickets(sortedRecentTickets)
                }, {
                    logger.warn("Error fetch tickets", it)
                })
    }

    interface Listener {
        fun onRecentTickets(tickets: List<TicketUseHistory>)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }
}