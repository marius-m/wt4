package lt.markmerkk.entities

import lt.markmerkk.Const

data class Ticket(
        val id: Long = Const.NO_ID,
        val code: TicketCode = TicketCode.asEmpty(),
        val description: String = "",
        val parentId: Long = Const.NO_ID, // todo up of removal
        val status: String,
        val assigneeName: String,
        val reporterName: String,
        val isWatching: Boolean,
        val parentCode: TicketCode,
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
                    status = "",
                    parentCode = TicketCode.asEmpty(),
                    assigneeName = "",
                    reporterName = "",
                    isWatching = false,
                    remoteData = remoteData
            )
        }

        fun fromRemoteData(
                code: String,
                description: String,
                status: String,
                assigneeName: String,
                reporterName: String,
                isWatching: Boolean,
                parentCode: String,
                remoteData: RemoteData?
        ): Ticket {
            return Ticket(
                    code = TicketCode.new(code),
                    description = description,
                    status = status,
                    assigneeName = assigneeName,
                    reporterName = reporterName,
                    isWatching = isWatching,
                    parentCode = TicketCode.new(parentCode),
                    remoteData = remoteData
            )
        }
    }
}

fun Ticket.markAsError(
        errorMessage: String
): Ticket {
    return Ticket(
            id = id,
            code = code,
            description = description,
            parentId = parentId,
            status = status,
            assigneeName = assigneeName,
            reporterName = reporterName,
            isWatching = isWatching,
            parentCode = parentCode,
            remoteData = remoteData.markAsError(errorMessage)
    )
}
