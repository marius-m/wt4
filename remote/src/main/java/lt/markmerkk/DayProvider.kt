package lt.markmerkk

import org.joda.time.LocalDate

/**
 * Day gap time provider
 */
interface DayProvider {

    /**
     * Start of day. Ex.: start=1970-01-01, end=1970-01-02
     */
    fun startAsDate(): LocalDate

    /**
     * Next day's start. Ex.: start=1970-01-01, end=1970-01-02
     */
    fun endAsDate(): LocalDate

    /**
     * Start of day. Ex.: start=1970-01-01 00:00, end=1970-01-02 00:00
     */
    fun startMillis(): Long

    /**
     * Next day's start. Ex.: start=1970-01-01 00:00, end=1970-01-02 00:00
     */
    fun endMillis(): Long
}