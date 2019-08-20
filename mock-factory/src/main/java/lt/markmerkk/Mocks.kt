package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.entities.*
import org.joda.time.DateTime

object Mocks {

    fun createTicket(
            id: Long = Const.NO_ID,
            code: String = "TTS-123",
            description: String = "valid_descriotion",
            parentId: Long = -1,
            remoteData: RemoteData? = null
    ): Ticket {
        return Ticket(
                id = id,
                code = TicketCode.new(code),
                description = description,
                parentId = parentId,
                remoteData = remoteData
        )
    }

    fun createBasicLog(
            timeProvider: TimeProvider,
            localId: Long = 1L
    ): Log {
        val now = timeProvider.nowMillis()
        return createLog(
                timeProvider = timeProvider,
                id = localId
        )
    }

    fun createBasicLogRemote(
            timeProvider: TimeProvider,
            localId: Long = 1L,
            remoteId: Long = 2L
    ): Log {
        val now = timeProvider.nowMillis()
        return createLog(
                timeProvider = timeProvider,
                id = localId,
                remoteData = createRemoteData(
                        timeProvider,
                        remoteId = remoteId
                )
        )
    }

    fun createLog(
            timeProvider: TimeProvider,
            id: Long = 1L,
            start: DateTime = timeProvider.now(),
            end: DateTime = timeProvider.now().plusMinutes(10),
            code: String = "DEV-123",
            comment: String = "valid_comment",
            remoteData: RemoteData? = null
    ): Log {
        val now = timeProvider.nowMillis()
        return Log.createAsTestable(
                timeProvider = timeProvider,
                id = id,
                start = start,
                end = end,
                code = code,
                comment = comment,
                remoteData = remoteData
        )
    }

    fun createRemoteData(
            timeProvider: TimeProvider,
            remoteId: Long = 1L,
            isDeleted: Boolean = false,
            isDirty: Boolean = false,
            isError: Boolean = false,
            errorMessage: String = "",
            fetchTime: Long = timeProvider.nowMillis(),
            url: String = ""
    ): RemoteData {
        val now = timeProvider.nowMillis()
        return RemoteData(
                remoteId = remoteId,
                isDeleted = isDeleted,
                isDirty = isDirty,
                isError = isError,
                errorMessage = errorMessage,
                fetchTime = fetchTime,
                url = url
        )
    }

    fun createLocalLog(
            timeProvider: TimeProvider,
            start: DateTime = timeProvider.now().plusMinutes(1),
            end: DateTime = timeProvider.now().plusMinutes(10),
            task: String = "DEV-123",
            comment: String = "valid_comment"
    ): SimpleLog {
        val now = timeProvider.now().plusMinutes(1).millis
        return SimpleLogBuilder(now)
                .setStart(timeProvider.roundMillis(start))
                .setEnd(timeProvider.roundMillis(end))
                .setTask(task)
                .setComment(comment)
                .build()
    }

    fun mockRemoteLog(
            timeProvider: TimeProvider,
            task: String,
            start: DateTime = timeProvider.now(),
            end: DateTime = timeProvider.now().plusMinutes(10),
            comment: String = "valid_comment",
            remoteId: Long = 1,
            localId: Long = 1
    ): SimpleLog {
        val log: SimpleLog = mock()
        doReturn(task).whenever(log).task
        doReturn(timeProvider.roundMillis(start)).whenever(log).start
        doReturn(timeProvider.roundMillis(end)).whenever(log).end
        doReturn(comment).whenever(log).comment
        doReturn(remoteId).whenever(log).id
        doReturn(localId).whenever(log)._id
        doReturn(true).whenever(log).isRemote
        doReturn(false).whenever(log).isDirty
        doReturn(false).whenever(log).isDeleted
        return log
    }

}