package lt.markmerkk.widgets.tickets

import lt.markmerkk.entities.Ticket

class TicketViewModel(
        val ticket: Ticket
) {

    val code: String = ticket.code.code
    val description: String = ticket.description

}