package lt.markmerkk.widgets.tickets

import lt.markmerkk.entities.Ticket

data class TicketViewModelBasic(
        val ticket: Ticket
) {
    val code: String = ticket.code.code
    val description: String = ticket.description
}