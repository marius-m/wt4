package lt.markmerkk.db2

import lt.markmerkk.entities.Ticket

interface TicketRepository {

    fun allTickets(): List<Ticket>

}