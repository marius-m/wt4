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
        return "[WORKLOG(localId=$id) / $shortFormatStart + $shortFormatEnd = $shortFormatDuration / $shortRemoteMessage]"
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
                remoteData = RemoteData.fromRemote(
                        fetchTime = fetchTime.millis,
                        url = url
                )
        )
    }

    fun markAsDeleted(): Log {
        return Log(
                id = id,
                time = time,
                code = code,
                comment = comment,
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
                remoteData: RemoteData?
        ): Log {
            return Log(
                    id = id,
                    time = LogTime.fromRaw(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment,
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
                url: String
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
                remoteData: RemoteData?
        ): Log {
            return Log(
                    id = id,
                    time = LogTime.fromRaw(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment,
                    remoteData = remoteData
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
                remoteData: RemoteData?
        ): Log {
            return Log(
                    id = id,
                    time = LogTime.from(timeProvider, start, end),
                    code = TicketCode.new(code),
                    comment = comment,
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
