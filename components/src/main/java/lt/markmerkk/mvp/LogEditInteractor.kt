package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import org.joda.time.DateTime

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
            startInDateTime: DateTime,
            endInDateTime: DateTime
    ): SimpleLog

    /**
     * Convenience method for overloaded one
     */
    @Throws(IllegalArgumentException::class)
    fun updateTimeConvenience(
            currentEntity: SimpleLog,
            startInDateTime: DateTime,
            endInDateTime: DateTime,
            task: String,
            comment: String
    ): SimpleLog

    /**
     * Updates and returns new entity or throws otherwise
     */
    @Throws(IllegalArgumentException::class)
    fun updateCurrentEntity(
            currentEntity: SimpleLog,
            startInMillis: Long,
            endInMillis: Long,
            task: String,
            comment: String
    ): SimpleLog

    /**
     * Update entity in database
     */
    fun update(
            entityToSave: SimpleLog
    )

    /**
     * Create a new entity to database
     */
    fun create(
            entityToSave: SimpleLog
    )

}