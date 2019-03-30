package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Responsible for finding [Ticket] by input code
 */
class TicketInfoLoader(
        private val listener: Listener,
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val waitScheduler: Scheduler,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private val inputCodeSubject = BehaviorSubject.create<String>("")
    private var searchSubscription: Subscription? = null
    private var inputSubscription: Subscription? = null

    fun onAttach() {
        inputSubscription = inputCodeSubject
                .throttleLast(INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS, ioScheduler)
                .subscribeOn(waitScheduler)
                .observeOn(uiScheduler)
                .subscribe {
                    findTicket(it)
                }
    }

    fun onDetach() {
        searchSubscription?.unsubscribe()
        inputSubscription?.unsubscribe()
    }

    fun changeInputCode(inputCode: String) {
        inputCodeSubject.onNext(inputCode)
    }

    fun findTicket(inputCode: String) {
        val ticketCode = TicketCode.new(inputCode)
        if (ticketCode.isEmpty()) {
            return
        }
        searchSubscription?.unsubscribe()
        searchSubscription = ticketsDatabaseRepo.findTicketsByCode(inputCode)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({ tickets ->
                    if (tickets.isNotEmpty()) {
                        listener.onTicketFound(tickets.first())
                    } else {
                        listener.onNoTicket()
                    }
                }, {
                    listener.onNoTicket()
                })
    }

    interface Listener {
        fun onTicketFound(ticket: Ticket)
        fun onNoTicket()
    }

    companion object {
        const val INPUT_THROTTLE_MILLIS = 500L
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }

}