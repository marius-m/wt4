package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import java.time.LocalDateTime

/**
 * Responsible for updating log data
 */
interface LogEditInteractor {

    /**
     * Convenience method for overloaded one
     */
    @Throws(IllegalArgumentException::class)
    fun updateDateTime(
            currentEntity: SimpleLog,
            startInDateTime: LocalDateTime,
            endInDateTime: LocalDateTime
    ): SimpleLog

    /**
     * Convenience method for overloaded one
     */
    @Throws(IllegalArgumentException::class)
    fun updateTimeConvenience(
            currentEntity: SimpleLog,
            startInDateTime: LocalDateTime,
            endInDateTime: LocalDateTime,
            task: String,
            comment: String
    ): SimpleLog

    /**
     * Updates and returns new entity or throws otherwise
     */
    @Throws(IllegalArgumentException::class)
    fun update(
            currentEntity: SimpleLog,
            startInMillis: Long,
            endInMillis: Long,
            task: String,
            comment: String
    ): SimpleLog

    /**
     * Saves entity to databse
     */
    fun save(
            entityToSave: SimpleLog
    )

}