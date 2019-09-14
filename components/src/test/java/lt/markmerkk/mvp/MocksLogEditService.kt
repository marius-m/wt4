package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.TimeMachine
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.joda.time.DateTime

object MocksLogEditService {

    fun createValidLogWithDate(): SimpleLog {
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        return createValidLogWithDate(fakeStart, fakeEnd)
    }

    fun createValidLogWithDate(
            fakeStart: DateTime,
            fakeEnd: DateTime
    ): SimpleLog {
        val startMillis = fakeStart.millis
        val endMillis = fakeEnd.millis
        val simpleLog = SimpleLogBuilder()
                .setStart(startMillis)
                .setEnd(endMillis)
                .setTask("WT-123")
                .setComment("valid_comment")
                .build()
        return simpleLog
    }

    fun mockValidLogWith(
            fakeStart: DateTime,
            fakeEnd: DateTime,
            task: String,
            comment: String
    ): SimpleLog {
        val simpleLog: SimpleLog = mock()
        val startMillis = fakeStart.millis
        val endMillis = fakeEnd.millis
        doReturn(startMillis).whenever(simpleLog).start
        doReturn(endMillis).whenever(simpleLog).end
        doReturn(task).whenever(simpleLog).task
        doReturn(comment).whenever(simpleLog).comment
        return simpleLog
    }

    fun mockValidLogWith(
            task: String,
            comment: String
    ): SimpleLog {
        val simpleLog: SimpleLog = mock()
        val startMillis = TimeMachine.now().millis
        val endMillis = TimeMachine.now().millis
        doReturn(startMillis).whenever(simpleLog).start
        doReturn(endMillis).whenever(simpleLog).end
        doReturn(task).whenever(simpleLog).task
        doReturn(comment).whenever(simpleLog).comment
        return simpleLog
    }

    fun mockRemoteLog(
            remoteId: Long = 1L
    ): SimpleLog {
        val simpleLog: SimpleLog = mock()
        val startMillis = TimeMachine.now().millis
        val endMillis = TimeMachine.now().millis
        doReturn(startMillis).whenever(simpleLog).start
        doReturn(endMillis).whenever(simpleLog).end
        doReturn("valid_task").whenever(simpleLog).task
        doReturn("valid_comment").whenever(simpleLog).comment
        doReturn(remoteId).whenever(simpleLog).id
        doReturn(true).whenever(simpleLog).isRemote()
        return simpleLog
    }

}