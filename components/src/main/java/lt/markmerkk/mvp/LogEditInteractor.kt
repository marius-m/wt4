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
    fun update(
            currentEntity: SimpleLog,
            startInDateTime: LocalDateTime,
            endInDateTime: LocalDateTime
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

}