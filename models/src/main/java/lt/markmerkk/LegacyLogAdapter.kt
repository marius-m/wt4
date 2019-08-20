package lt.markmerkk

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder

/**
 * Temporary solution to link [SimpleLog] with [Log]
 */
object LegacyLogAdapter { }

fun SimpleLog?.toLogOrNull(timeProvider: TimeProvider): Log? {
    if (this == null) {
        return null
    }
    return this.toLog(timeProvider)
}

fun SimpleLog.toLog(
        timeProvider: TimeProvider
): Log {
    return Log.new(
            timeProvider = timeProvider,
            id = this._id,
            start = this.start,
            end = this.end,
            code = this.task,
            comment = this.comment,
            remoteData = RemoteData.new(
                    isDeleted = this.isDeleted,
                    isDirty = this.isDirty,
                    isError = this.isError,
                    errorMessage = this.errorMessage,
                    fetchTime = this.download_millis,
                    url = this.uri ?: ""
            )
    )
}

fun Log?.toLegacyLogOrNull(timeProvider: TimeProvider): SimpleLog? {
    if (this == null) {
        return null
    }
    return this.toLegacyLog(timeProvider)
}

fun Log.toLegacyLog(
        timeProvider: TimeProvider
): SimpleLog {
    val remoteData = this.remoteData ?: RemoteData.asEmpty()
    return SimpleLogBuilder().buildLegacy(
        timeProvider.nowMillis(),
            this.id,
            this.time.startAsRaw,
            this.time.endAsRaw,
            this.time.durationAsRaw,
            this.code.code,
            this.comment,
            remoteData.remoteId,
            remoteData.isDeleted,
            remoteData.isDirty,
            remoteData.isError,
            remoteData.errorMessage,
            remoteData.fetchTime,
            remoteData.url
    )
}
