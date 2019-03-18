package lt.markmerkk.tickets

import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Ticket
import rx.Scheduler
import rx.Subscription

/**
 * Responsible for loading tickets
 */
class TicketLoader(
        private val listener: Listener,
        private val ticketsInteractor: TicketsInteractor,
        private val timeProvider: TimeProvider,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subscription: Subscription? = null

    fun onAttach() {}
    fun onDetach() {
        subscription?.unsubscribe()
    }

    fun loadTickets() {
        subscription?.unsubscribe()
        subscription = ticketsInteractor.searchRemoteTickets(timeProvider.now())
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.isNotEmpty()) {
                        listener.onTicketsReady(it)
                    } else {
                        listener.onNoTickets()
                    }
                }, {
                    listener.onError(it)
                })
    }

    interface Listener {
        fun onTicketsReady(tickets: List<Ticket>)
        fun onNoTickets()
        fun onError(throwable: Throwable)
    }

}