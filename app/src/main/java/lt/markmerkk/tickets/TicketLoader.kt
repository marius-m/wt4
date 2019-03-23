package lt.markmerkk.tickets

import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Ticket
import me.xdrop.fuzzywuzzy.FuzzySearch
import rx.Scheduler
import rx.Subscription

/**
 * Responsible for loading tickets
 */
class TicketLoader(
        private val listener: Listener,
        private val ticketsRepository: TicketsRepository,
        private val timeProvider: TimeProvider,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subscription: Subscription? = null
    private var tickets: List<Ticket> = emptyList()

    fun onAttach() {}
    fun onDetach() {
        subscription?.unsubscribe()
    }

    fun loadTickets() {
        subscription?.unsubscribe()
        subscription = ticketsRepository.tickets(ticketRefreshTimeoutInDays = 1)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.isNotEmpty()) {
                        listener.onTicketsReady(it)
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
            listener.onTicketsReady(tickets)
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
        listener.onTicketsReady(ticketResult)
    }

    interface Listener {
        fun onTicketsReady(tickets: List<Ticket>)
        fun onNoTickets()
        fun onError(throwable: Throwable)
    }

    data class TicketSearchResult(
            val ticket: Ticket,
            val score: Int
    )

}