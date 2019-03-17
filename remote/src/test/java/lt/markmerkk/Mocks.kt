package lt.markmerkk

import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode

object Mocks {

    fun createTicket(
            id: Long = Const.NO_ID,
            code: String = "TTS-123",
            description: String = "valid_descriotion",
            parentTicket: Ticket? = null,
            remoteData: RemoteData? = null
    ): Ticket {
        return Ticket(
                id = id,
                code = TicketCode.new(code),
                description = description,
                parentTicket = parentTicket,
                remoteData = remoteData
        )
    }

}