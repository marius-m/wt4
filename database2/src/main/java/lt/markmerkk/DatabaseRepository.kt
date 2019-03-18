package lt.markmerkk

import lt.markmerkk.entities.Ticket

interface DatabaseRepository {
    fun loadTickets(): List<Ticket>
    fun insertOrUpdate(ticket: Ticket): Int
}