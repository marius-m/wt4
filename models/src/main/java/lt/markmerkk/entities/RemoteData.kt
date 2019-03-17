package lt.markmerkk.entities

import lt.markmerkk.Const
import lt.markmerkk.utils.UriUtils

data class RemoteData(
        val remoteId: Long,
        val isDeleted: Boolean,
        val isDirty: Boolean,
        val isError: Boolean,
        val errorMessage: String,
        val fetchTime: Long,
        val url: String
) {
    companion object {

        fun asEmpty(): RemoteData {
            return RemoteData(
                    remoteId = Const.NO_ID,
                    isDeleted = false,
                    isDirty = false,
                    isError = false,
                    errorMessage = "",
                    fetchTime = 0,
                    url = ""
            )
        }

        fun new(
                remoteId: Long,
                isDeleted: Boolean = false,
                isDirty: Boolean = false,
                isError: Boolean = false,
                errorMessage: String = "",
                fetchTime: Long,
                uri: String
        ): RemoteData? {
            if (remoteId == Const.NO_ID) {
                return null
            }
            return RemoteData(
                    remoteId = remoteId,
                    isDeleted = isDeleted,
                    isDirty = isDirty,
                    isError = isError,
                    errorMessage = errorMessage,
                    fetchTime = fetchTime,
                    url = uri
            )
        }

        fun fromRemote(
                remoteIdUrl: String,
                fetchTime: Long,
                uri: String
        ): RemoteData? {
            val remoteId = UriUtils.parseUri(remoteIdUrl)
            if (remoteId == Const.NO_ID) {
                return null
            }
            return RemoteData(
                    remoteId = remoteId,
                    isDeleted = false,
                    isDirty = false,
                    isError = false,
                    errorMessage = "",
                    fetchTime = fetchTime,
                    url = uri
            )
        }
    }
}

fun RemoteData?.markAsError(errorMessage: String): RemoteData? {
    if (this == null) return null
    return RemoteData(
            remoteId = remoteId,
            isDeleted = isDeleted,
            isDirty = isDirty,
            isError = true,
            errorMessage = errorMessage,
            fetchTime = fetchTime,
            url = url
    )
}