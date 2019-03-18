package lt.markmerkk.db2

import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.entities.Ticket

class TicketRepositoryImpl(
        private val dbRepoTickets: TicketsDatabaseRepo
): TicketRepository {

    override fun allTickets(): List<Ticket> {
        return dbRepoTickets.loadTickets()
    }

}