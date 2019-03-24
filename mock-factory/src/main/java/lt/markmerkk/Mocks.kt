package lt.markmerkk

import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode

object Mocks {

    fun createTicket(
            id: Long = Const.NO_ID,
            code: String = "TTS-123",
            description: String = "valid_descriotion",
            parentId: Long = -1,
            remoteData: RemoteData? = null
    ): Ticket {
        return Ticket(
                id = id,
                code = TicketCode.new(code),
                description = description,
                parentId = parentId,
                remoteData = remoteData
        )
    }

}