package lt.markmerkk

import lt.markmerkk.entities.Ticket

interface DatabaseRepository {
    fun loadTickets(): List<Ticket>
    fun ticketByRemoteId(remoteId: Long): Ticket?
    fun insertTicket(ticket: Ticket)
    fun updateTicket(oldticket: Ticket, newTicket: Ticket)
    fun insertOrUpdate(ticket: Ticket): Boolean
    fun markTicketAsError(ticket: Ticket)
}