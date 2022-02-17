package lt.markmerkk

import lt.markmerkk.entities.Log
import org.joda.time.DateTime
import org.joda.time.Duration

// todo change name to ActiveDisplayRepository
interface LogRepository {

    /**
     * Get currently loaded logs
     */
    // todo active logs
    val data: List<Log>

    val displayType: DisplayTypeLength

    fun changeDisplayType(displayType: DisplayTypeLength)

    // todo change to LocalDate
    // todo change name to activeDate
    // Maybe change to date range?
    val targetDate: DateTime

    // todo change to LocalDate
    fun changeActiveDate(newActiveDate: DateTime)

    fun insertOrUpdate(log: Log): Long

    fun delete(log: Log): Long

    fun update(log: Log): Long

    fun notifyDataChange()

    /**
     * Finds item by id or null
     */
    // todo disable exposure, should use WorklogStorage directly
    fun findByIdOrNull(id: Long): Log?

    fun totalInMillis(): Long

    fun totalAsDuration(): Duration

}
