package lt.markmerkk.entities

import lt.markmerkk.Const

data class Log(
        val id: Long = Const.NO_ID,
        val start: Long = 0,
        val end: Long = 0,
        val duration: Long = 0,
        val code: String = "",
        val comment: String = "",
        val remoteData: RemoteData? = null
) {

    companion object {
        fun new(
                start: Long,
                end: Long,
                duration: Long,
                code: String,
                comment: String,
                remoteData: RemoteData?
        ): Log {
            return Log(
                    start = start,
                    end = end,
                    duration = duration,
                    code = code,
                    comment = comment,
                    remoteData = remoteData
            )
        }
    }
}