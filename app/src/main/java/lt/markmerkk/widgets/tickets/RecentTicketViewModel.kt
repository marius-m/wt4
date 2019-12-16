package lt.markmerkk.widgets.tickets

import lt.markmerkk.entities.TicketUseHistory

data class RecentTicketViewModel(
        val ticketUseHistory: TicketUseHistory
) {
    val code: String = ticketUseHistory.code.code
    val description: String = ticketUseHistory.description
}