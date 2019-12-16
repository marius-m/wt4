package lt.markmerkk.entities

data class TicketUseHistory(
    val ticketCode: TicketCode,
    val lastUsed: Long
)