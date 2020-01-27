package lt.markmerkk.tickets

import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketStatus
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription

class TicketStatusesLoader(
        private val listener: Listener,
        private val ticketApi: TicketApi,
        private val timeProvider: TimeProvider,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var subsTicketStatuses: Subscription? = null

    fun onAttach() {}

    fun onDetach() {
        subsTicketStatuses?.unsubscribe()
    }

    fun fetchTicketStatuses() {
        subsTicketStatuses?.unsubscribe()
        val now = timeProvider.now()
        subsTicketStatuses = ticketApi.fetchProjectStatusesAndCache(now)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { listener.showProgress() }
                .doAfterTerminate { listener.hideProgress() }
                .subscribe({
                    if (it.isNotEmpty()) {
                        listener.showStatuses(it)
                    } else {
                        listener.hideProgress()
                    }
                }, {
                    logger.warn("Error showing statuses", it)
                })
    }

    interface Listener {
        fun showProgress()
        fun hideProgress()
        fun showStatuses(statuses: List<TicketStatus>)
        fun noStatuses()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TicketStatusesLoader::class.java)!!
    }

}