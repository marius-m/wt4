package lt.markmerkk.db2

import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import lt.markmerkk.tickets.TicketsNetworkRepo

class TicketRepository(
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val ticketsNetworkRepo: TicketsNetworkRepo,
        private val userSettings: UserSettings
) {

    fun tickets(): List<Ticket> {
        TODO()
    }

}