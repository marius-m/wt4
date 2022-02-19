package lt.markmerkk

import lt.markmerkk.entities.DateRange
import lt.markmerkk.entities.Log
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate

/**
 * Provides data of actively displayed data on screen
 */
interface ActiveDisplayRepository {

    val displayLogs: List<Log>

    val displayType: DisplayTypeLength

    fun changeDisplayType(displayType: DisplayTypeLength)

    val displayDateRange: DateRange

    fun changeDisplayDate(newDate: LocalDate)

    fun prevDisplayDate()

    fun nextDisplayDate()

    fun insertOrUpdate(log: Log): Long

    fun delete(log: Log): Long

    fun update(log: Log): Long

    fun notifyDataChange()

    fun totalInMillis(): Long

    fun totalAsDuration(): Duration

}
