package lt.markmerkk.export.entities

import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log

data class ImportLogResponse(
        val start: Long,
        val end: Long,
        val duration: Long?,
        val code: String?,
        val comment: String?
) {
    fun toLog(timeProvider: TimeProvider): Log {
        return Log.new(
            timeProvider= timeProvider,
                start = start,
                end = end,
                code = code ?: "",
                comment = comment ?: "",
                systemNote = "",
                remoteData = null
        )
    }
}