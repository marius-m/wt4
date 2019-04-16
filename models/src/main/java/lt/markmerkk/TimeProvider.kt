package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate

interface TimeProvider {
    val dateTimeZone: DateTimeZone
    val zoneId: java.time.ZoneId

    /**
     * @return instance of 'now' in joda datetime
     */
    fun now(): DateTime

    /**
     * @return instance of 'now' in java8 datetime
     */
    fun jNow(): java.time.LocalDateTime

    /**
     * Converts millis to java8 datetime
     */
    fun jLocalDateTimeFrom(millis: Long): java.time.LocalDateTime

    /**
     * Converts java8 datetime to millis
     */
    fun jMillisFrom(dateTime: java.time.LocalDateTime): Long

    companion object {

        /**
         * Converts joda [DateTime] to java8 [LocalDate]
         */
        fun toJavaLocalDate(jodaDateTime: DateTime): java.time.LocalDate {
            return java.time.LocalDate.of(
                    jodaDateTime.year,
                    jodaDateTime.monthOfYear,
                    jodaDateTime.dayOfMonth
            )
        }

        /**
         * Converts java8 [LocalDate] to joda [DateTime]
         */
        fun toJodaLocalDate(javaLocalDate: java.time.LocalDate): LocalDate {
            return LocalDate(
                    javaLocalDate.year,
                    javaLocalDate.monthValue,
                    javaLocalDate.dayOfMonth
            )
        }
    }

}
