package lt.markmerkk.entities

import lt.markmerkk.Const
import lt.markmerkk.utils.UriUtils
import org.joda.time.DateTime

data class Ticket(
        val id: Long = Const.NO_ID,
        val code: TicketCode = TicketCode.asEmpty(),
        val description: String = "",
        val parentId: Long = Const.NO_ID,
        val remoteData: RemoteData? = null
) {

    var parentTicket: Ticket? =null

    companion object {
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

        fun fromRemoteData(
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

fun Ticket.bindRemoteData(
        now: DateTime,
        remoteProjectKey: String,
        remoteDescription: String,
        remoteIdUrl: String,
        remoteUri: String
): Ticket {
    return Ticket(
            id = id,
            code = TicketCode.new(remoteProjectKey),
            description = remoteDescription,
            remoteData = RemoteData.new(
                    isDeleted = false,
                    isDirty = false,
                    isError = false,
                    errorMessage = "",
                    fetchTime = now.millis,
                    url = remoteUri
            )
    )
}

fun Ticket.markAsError(
        errorMessage: String
): Ticket {
    return Ticket(
            id = id,
            code = code,
            description = description,
            parentId = parentId,
            remoteData = remoteData.markAsError(errorMessage)
    )
}
