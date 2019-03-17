package lt.markmerkk.entities

import lt.markmerkk.Const

data class Ticket(
        val id: Long = Const.NO_ID,
        val code: TicketCode = TicketCode.asEmpty(),
        val description: String = "",
        val parentTicket: Ticket? = null,
        val remoteData: RemoteData? = null
) {
    companion object {
        // todo: Missing parent ticket binding
        fun new(
                code: String,
                description: String,
                remoteData: RemoteData?
        ): Ticket {
            return Ticket(
                    code = TicketCode.new(code),
                    description = description,
                    remoteData = remoteData
            )
        }
    }
}