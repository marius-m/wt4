package lt.markmerkk.db2

import lt.markmerkk.DatabaseRepository
import lt.markmerkk.entities.Ticket

class TicketRepositoryImpl(
        private val dbRepository: DatabaseRepository
): TicketRepository {

    override fun allTickets(): List<Ticket> {
        return dbRepository.loadTickets()
    }

}