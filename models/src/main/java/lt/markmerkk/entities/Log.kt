package lt.markmerkk.entities

import lt.markmerkk.Const
import lt.markmerkk.TimeProvider
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.DurationFieldType

data class Log private constructor(
        val id: Long = Const.NO_ID,
        val time: LogTime,
        val code: TicketCode,
        val comment: String,
        val systemNote: String,
        val author: String,
        val remoteData: RemoteData? = null
) {

    val isRemote: Boolean = remoteData != null
    val isMarkedForDeletion = remoteData?.isDeleted ?: false
    val canUpload = !comment.isEmpty()
            && !code.isEmpty()
            && time.duration.toStandardMinutes().minutes >= 1
            && !isRemote

    fun toStringShort(): String {
        val shortFormatStart = LogFormatters.shortFormat.print(time.start)
        val shortFormatEnd = LogFormatters.shortFormat.print(time.end)
        val shortFormatDuration = LogFormatters.humanReadableDuration(time.duration)
        val shortRemoteMessage = if (remoteData != null) {
            "isRemote: $isRemote (remoteId=${remoteData.remoteId})"
        } else {
            "isRemote: $isRemote"
        }
        return "[WORKLOG(localId=$id) / ${code.code} / $shortFormatStart + $shortFormatEnd = $shortFormatDuration / $shortRemoteMessage]"
    }

    fun toStringLonger(): String {
        val shortFormatStart = LogFormatters.shortFormat.print(time.start)
        val shortFormatEnd = LogFormatters.shortFormat.print(time.end)
        val shortFormatDuration = LogFormatters.humanReadableDuration(time.duration)
        val shortRemoteMessage = if (remoteData != null) {
            "isRemote: $isRemote (remoteId=${remoteData.remoteId})"
        } else {
            "isRemote: $isRemote"
        }
        return "[WORKLOG(localId=$id) / ${code.code} / $shortFormatStart + $shortFormatEnd = $shortFormatDuration / $shortRemoteMessage / $comment]"
    }

    //region Factories

    fun appendRemoteData(
            timeProvider: TimeProvider,
            code: String,
            started: java.util.Date,
            comment: String?,
            timeSpentSeconds: Int,
            fetchTime: DateTime,
            url: String
    ): Log {
        val start = timeProvider.roundDateTime(started.time)
        val end = start.withFieldAdded(
                DurationFieldType.seconds(),
                timeSpentSeconds
        )
        return Log(
                id = id,
                time = LogTime.from(timeProvider, start, end),
                code = TicketCode.new(code),
                comment = comment ?: "",
                systemNote = systemNote,
                author = author,
                remoteData = RemoteData.fromRemote(
                        fetchTime = fetchTime.millis,
                        url = url
                )
        )
    }

    fun appendSystemNote(systemNote: String): Log {
        return Log(
                id = id,
                time = time,
                code = code,
                comment = comment,
                systemNote = systemNote,
                author = author,
                remoteData = remoteData
        )
    }

    fun clearTicketCode(): Log {
        return Log(
                id = id,
                time = time,
                code = TicketCode.asEmpty(),
                comment = comment,
                systemNote = systemNote,
                author = author,
                remoteData = remoteData
        )
    }

    fun markAsDeleted(): Log {
        return Log(
                id = id,
                time = time,
                code = code,
                comment = comment,
                systemNote = systemNote,
                author = author,
                remoteData = this.remoteData.markAsDelete()
        )
    }

    //endregion

    companion object {

        fun createAsEmpty(
                timeProvider: TimeProvider
        ): Log {
            val now = timeProvider.nowMillis()
            return Log(
                    time = LogTime.fromRaw(timeProvider, now, now),
                    code = TicketCode.new(""),
                    comment = "",
                    systemNote = "",
                    author = "",
                    remoteData = null
            )
        }

        fun new(
                timeProvider: TimeProvider,
                id: Long = Const.NO_ID,
                start: Long,
                end: Long,
                code: String,
                comment: String,
                systemNote: String,
                author: String,
                remoteData: RemoteData?
        ): Log {
            return Log(
                    id = id,
                    time = LogTime.fromRaw(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment,
                    systemNote = systemNote,
                    author = author,
                    remoteData = remoteData
            )
        }

        fun createFromRemoteData(
            timeProvider: TimeProvider,
                code: String,
                started: java.util.Date,
                comment: String?,
                timeSpentSeconds: Int,
                fetchTime: DateTime,
                url: String,
                author: String
        ): Log {
            val start = timeProvider.roundDateTime(started.time)
            val end = start.withFieldAdded(
                    DurationFieldType.seconds(),
                    timeSpentSeconds
            )
            return Log(
                    id = Const.NO_ID,
                    time = LogTime.from(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment ?: "",
                    systemNote = "",
                    author = author,
                    remoteData = RemoteData.fromRemote(
                            fetchTime = fetchTime.millis,
                            url = url
                    )
            )
        }

        fun createFromDatabase(
                timeProvider: TimeProvider,
                id: Long,
                start: Long,
                end: Long,
                code: String,
                comment: String,
                systemNote: String,
                author: String,
                remoteData: RemoteData?
        ): Log {
            return Log(
                    id = id,
                    time = LogTime.fromRaw(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment,
                    systemNote = systemNote,
                    author = author,
                    remoteData = remoteData
            )
        }

        fun Log.clone(
            timeProvider: TimeProvider,
            start: DateTime = this.time.start,
            end: DateTime = this.time.end,
            code: TicketCode = this.code,
            comment: String = this.comment
        ): Log {
            return Log(
                id = id,
                time = LogTime.from(timeProvider, start, end),
                code = code,
                comment = comment,
                systemNote = systemNote,
                author = author,
                remoteData = remoteData
            )
        }

        fun Log.cloneAsNewLocal(
            timeProvider: TimeProvider,
            start: DateTime = this.time.start,
            end: DateTime = this.time.end,
            code: TicketCode = this.code,
            comment: String = this.comment
        ): Log {
            return Log(
                id = Const.NO_ID,
                time = LogTime.from(timeProvider, start, end),
                code = code,
                comment = comment,
                systemNote = "",
                author = "",
                remoteData = null
            )
        }

        // Should only be used for testing
        fun createAsTestable(
                timeProvider: TimeProvider,
                id: Long,
                start: DateTime,
                end: DateTime,
                code: String,
                comment: String,
                systemNote: String,
                author: String,
                remoteData: RemoteData?
        ): Log {
            return Log(
                    id = id,
                    time = LogTime.from(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment,
                    systemNote = systemNote,
                    author = author,
                    remoteData = remoteData
            )
        }

    }
}

data class LogTime(
    val start: DateTime,
    val end: DateTime,
    val duration: Duration,
    val startAsRaw: Long,
    val endAsRaw: Long,
    val durationAsRaw: Long
) {
    companion object {

        fun from(
                timeProvider: TimeProvider,
                start: DateTime,
                end: DateTime
        ): LogTime {
            return fromRaw(
                    timeProvider,
                    timeProvider.roundMillis(start),
                    timeProvider.roundMillis(end)
            )
        }

        fun fromRaw(
                timeProvider: TimeProvider,
                start: Long,
                end: Long
        ): LogTime {
            val startDateTime = timeProvider.roundDateTime(start)
            val endDateTime = timeProvider.roundDateTime(end)
            if (startDateTime.isAfter(endDateTime)) {
                return LogTime(
                        start = startDateTime,
                        end = startDateTime,
                        duration = Duration.ZERO,
                        startAsRaw = timeProvider.roundMillis(startDateTime),
                        endAsRaw = timeProvider.roundMillis(endDateTime),
                        durationAsRaw = Duration.ZERO.millis
                )
            }
            val duration = Duration(startDateTime, endDateTime)
            return LogTime(
                    start = startDateTime,
                    end = endDateTime,
                    duration = duration,
                    startAsRaw = timeProvider.roundMillis(startDateTime),
                    endAsRaw = timeProvider.roundMillis(endDateTime),
                    durationAsRaw = duration.millis
            )
        }
    }

}

fun Log.toTimeGap(): TimeGap {
    return TimeGap.from(
        time.start,
        time.end
    )
}

fun Log.toTimeGapRounded(timeProvider: TimeProvider): TimeGap {
    return TimeGap.from(
        timeProvider.roundDateTime(time.start.millis),
        timeProvider.roundDateTime(time.end.millis)
    )
}
