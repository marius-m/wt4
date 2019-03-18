package lt.markmerkk.tickets

import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import org.joda.time.DateTime
import rx.Single

class TicketsRepository(
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val ticketsNetworkRepo: TicketsNetworkRepo,
        private val userSettings: UserSettings,
        private val timeProvider: TimeProvider
) {

    fun tickets(
            ticketRefreshTimeoutInDays: Int
    ): Single<List<Ticket>> {
        val now = timeProvider.now()
        val isFreshEnough = isFreshEnough(
                lastTimeout = DateTime(userSettings.ticketLastUpdate),
                timeoutInDays = ticketRefreshTimeoutInDays,
                now = now
        )
        return Single.just(isFreshEnough)
                .flatMap {
                    if (isFreshEnough) {
                        Single.just(ticketsDatabaseRepo.loadTickets())
                    } else {
                        ticketsNetworkRepo.searchRemoteTicketsAndCache(now)
                                .doOnSuccess { userSettings.ticketLastUpdate = now.millis }
                    }
                }
    }


    companion object {

        /**
         * Checks if timeout has expired
         */
        fun isFreshEnough(
                lastTimeout: DateTime,
                timeoutInDays: Int,
                now: DateTime
        ): Boolean {
            return DateTime(lastTimeout).plusDays(timeoutInDays)
                    .isAfter(now)
        }
    }

}