package lt.markmerkk.entities

import lt.markmerkk.Const
import lt.markmerkk.utils.UriUtils

data class RemoteData(
        val remoteId: Long,
        val isDeleted: Boolean, // todo up for removal
        val isDirty: Boolean, // todo up for removal
        val errorMessage: String,
        val fetchTime: Long,
        val url: String
) {

    val isError = errorMessage.isNotEmpty()

    companion object {

        fun asEmpty(): RemoteData {
            return RemoteData(
                    remoteId = Const.NO_ID,
                    isDeleted = false,
                    isDirty = true,
                    errorMessage = "",
                    fetchTime = 0,
                    url = ""
            )
        }

        fun new(
                isDeleted: Boolean = false,
                isDirty: Boolean = true,
                isError: Boolean = false,
                errorMessage: String = "",
                fetchTime: Long,
                url: String
        ): RemoteData? {
            val remoteId = UriUtils.parseUri(url)
            if (remoteId == Const.NO_ID) {
                return null
            }
            return RemoteData(
                    remoteId = remoteId,
                    isDeleted = isDeleted,
                    isDirty = isDirty,
                    errorMessage = errorMessage,
                    fetchTime = fetchTime,
                    url = url
            )
        }

        fun fromRemote(
                fetchTime: Long,
                url: String
        ): RemoteData? {
            val remoteId = UriUtils.parseUri(url)
            if (remoteId == Const.NO_ID) {
                return null
            }
            return RemoteData(
                    remoteId = remoteId,
                    isDeleted = false,
                    isDirty = false,
                    errorMessage = "",
                    fetchTime = fetchTime,
                    url = url
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
            errorMessage = errorMessage,
            fetchTime = fetchTime,
            url = url
    )
}

fun RemoteData?.markAsDelete(): RemoteData? {
    if (this == null) return null
    return RemoteData(
            remoteId = remoteId,
            isDeleted = true,
            isDirty = isDirty,
            errorMessage = errorMessage,
            fetchTime = fetchTime,
            url = url
    )
}
