package lt.markmerkk.entities

import lt.markmerkk.Const
import lt.markmerkk.utils.UriUtils
import org.joda.time.DateTime

data class Ticket(
        val id: Long = Const.NO_ID,
        val code: TicketCode = TicketCode.asEmpty(),
        val description: String = "",
        val parentTicket: Ticket? = null,
        val remoteData: RemoteData? = null
) {

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
                    remoteId = UriUtils.parseUri(remoteIdUrl),
                    isDeleted = false,
                    isDirty = false,
                    isError = false,
                    errorMessage = "",
                    fetchTime = now.millis,
                    uri = remoteUri
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
            parentTicket = parentTicket,
            remoteData = remoteData.markAsError(errorMessage)
    )
}
