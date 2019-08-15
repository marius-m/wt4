package lt.markmerkk.widgets.tickets

import lt.markmerkk.entities.Ticket

class TicketViewModel(
        val ticket: Ticket,
        val filterScore: Int
) {

    val filterScoreAsDouble = filterScore.toDouble() / 100
    val code: String = ticket.code.code
    val description: String = ticket.description
}