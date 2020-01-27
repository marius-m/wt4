package lt.markmerkk.entities

import org.joda.time.DateTime

data class TicketUseHistory(
        val code: TicketCode,
        val description: String,
        val lastUsed: DateTime
) {
    fun appendDescription(description: String): TicketUseHistory {
        return TicketUseHistory(
                code = this.code,
                description = description,
                lastUsed = this.lastUsed
        )
    }
}