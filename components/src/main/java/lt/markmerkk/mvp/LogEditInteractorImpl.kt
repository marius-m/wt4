package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import java.time.LocalDateTime
import java.time.ZoneId

class LogEditInteractorImpl : LogEditInteractor {

    override fun update(
            currentEntity: SimpleLog,
            startInDateTime: LocalDateTime,
            endInDateTime: LocalDateTime
    ): SimpleLog {
        val startInMillis = startInDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endInMillis = endInDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return update(
                currentEntity,
                startInMillis,
                endInMillis,
                currentEntity.task,
                currentEntity.comment
        )
    }

    override fun update(
            currentEntity: SimpleLog,
            startInMillis: Long,
            endInMillis: Long,
            task: String,
            comment: String
    ): SimpleLog {
        return SimpleLogBuilder(currentEntity)
                .setStart(startInMillis)
                .setEnd(endInMillis)
                .setTask(task)
                .setComment(comment)
                .build()
    }
}