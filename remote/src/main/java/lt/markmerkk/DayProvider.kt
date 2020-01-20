package lt.markmerkk

import org.joda.time.LocalDate

/**
 * Day gap time provider
 */
interface DayProvider {
    fun startAsDate(): LocalDate
    fun endAsDate(): LocalDate

    fun startMillis(): Long
    fun endMillis(): Long
}