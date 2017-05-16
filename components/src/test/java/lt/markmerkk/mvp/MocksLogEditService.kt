package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * @author mariusmerkevicius
 * @since 2017-05-11
 */
object MocksLogEditService {

    fun buildValidLog(): SimpleLog {
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        return buildValidLog(fakeStart, fakeEnd)
    }

    fun buildValidLog(
            fakeStart: LocalDateTime,
            fakeEnd: LocalDateTime
    ): SimpleLog {
        val startMillis = fakeStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = fakeEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val simpleLog = SimpleLogBuilder()
                .setStart(startMillis)
                .setEnd(endMillis)
                .setTask("WT-123")
                .setComment("valid_comment")
                .build()
        return simpleLog
    }

    fun mockValidLog(
            fakeStart: LocalDateTime,
            fakeEnd: LocalDateTime,
            task: String,
            comment: String
    ): SimpleLog {
        val simpleLog: SimpleLog = mock()
        val startMillis = fakeStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = fakeEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        doReturn(startMillis).whenever(simpleLog).start
        doReturn(endMillis).whenever(simpleLog).end
        doReturn(task).whenever(simpleLog).task
        doReturn(comment).whenever(simpleLog).comment
        return simpleLog
    }

}