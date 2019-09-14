package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketStorage
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Responsible for finding [Ticket] by input code
 */
class TicketInfoLoader(
        private val listener: Listener,
        private val ticketStorage: TicketStorage,
        private val waitScheduler: Scheduler,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var searchSubscription: Subscription? = null
    private var inputSubscription: Subscription? = null

    fun onAttach() { }

    fun onDetach() {
        searchSubscription?.unsubscribe()
        inputSubscription?.unsubscribe()
    }

    fun attachInputCodeAsStream(inputCode: Observable<String>) {
        inputSubscription = inputCode
                .throttleLast(INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS, ioScheduler)
                .subscribeOn(waitScheduler)
                .observeOn(uiScheduler)
                .subscribe {
                    findTicket(it)
                }
    }

    fun findTicket(inputCode: String) {
        val ticketCode = TicketCode.new(inputCode)
        if (ticketCode.isEmpty()) {
            listener.onNoTicket(inputCode)
            return
        }
        searchSubscription?.unsubscribe()
        searchSubscription = ticketStorage.findTicketsByCode(inputCode)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({ tickets ->
                    if (tickets.isNotEmpty()) {
                        listener.onTicketFound(tickets.first())
                    } else {
                        listener.onNoTicket(inputCode)
                    }
                }, {
                    listener.onNoTicket(inputCode)
                })
    }

    interface Listener {
        fun onTicketFound(ticket: Ticket)
        fun onNoTicket(searchTicket: String)
    }

    companion object {
        const val INPUT_THROTTLE_MILLIS = 500L
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }

}