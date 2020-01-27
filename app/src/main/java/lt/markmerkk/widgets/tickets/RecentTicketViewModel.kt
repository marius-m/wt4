package lt.markmerkk.widgets.tickets

import lt.markmerkk.entities.TicketUseHistory
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.Duration

data class RecentTicketViewModel(
        val now: DateTime,
        val ticketUseHistory: TicketUseHistory
) {
    val code: String = ticketUseHistory.code.code
    val description: String = ticketUseHistory.description
    val lastUsed: String = LogFormatters.humanReadableDuration(Duration(ticketUseHistory.lastUsed, now))
}