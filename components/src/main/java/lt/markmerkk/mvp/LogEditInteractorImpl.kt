package lt.markmerkk.mvp

import lt.markmerkk.IDataStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.joda.time.DateTime

class LogEditInteractorImpl(
        private val dataStorage: IDataStorage<SimpleLog>,
        private val timeProvider: TimeProvider
) : LogEditInteractor {

    override fun updateDateTime(
            currentEntity: SimpleLog,
            startInDateTime: DateTime,
            endInDateTime: DateTime
    ): SimpleLog {
        val startInMillis = timeProvider.millisFrom(startInDateTime)
        val endInMillis = timeProvider.millisFrom(endInDateTime)
        return updateCurrentEntity(
                currentEntity,
                startInMillis,
                endInMillis,
                currentEntity.task,
                currentEntity.comment
        )
    }

    override fun updateTimeConvenience(
            currentEntity: SimpleLog,
            startInDateTime: DateTime,
            endInDateTime: DateTime,
            task: String,
            comment: String
    ): SimpleLog {
        val startInMillis = timeProvider.millisFrom(startInDateTime)
        val endInMillis = timeProvider.millisFrom(endInDateTime)
        return updateCurrentEntity(
                currentEntity,
                startInMillis,
                endInMillis,
                task,
                comment
        )
    }

    override fun updateCurrentEntity(
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

    override fun update(entityToSave: SimpleLog) {
        dataStorage.update(entityToSave)
    }

    override fun create(entityToSave: SimpleLog) {
        dataStorage.insert(entityToSave)
    }
}