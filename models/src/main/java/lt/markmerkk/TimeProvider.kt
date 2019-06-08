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
    fun dateTimeFromMillis(millis: Long): DateTime

    /**
     * Converts java8 datetime to millis
     */
    fun millisFrom(dateTime: DateTime): Long

    /**
     * Converts millis to java8 datetime
     */
    fun jLocalDateTimeFrom(millis: Long): java.time.LocalDateTime

    /**
     * Converts java8 datetime to millis
     */
    fun jMillisFrom(dateTime: java.time.LocalDateTime): Long

    /**
     * Converts date time from java8 to joda DateTime
     */
    fun toJodaDateTime(
            javaLocalDate: java.time.LocalDate,
            javaLocalTime: java.time.LocalTime
    ): DateTime {
        return DateTime(
                javaLocalDate.year,
                javaLocalDate.monthValue,
                javaLocalDate.dayOfMonth,
                javaLocalTime.hour,
                javaLocalTime.minute,
                javaLocalTime.second,
                dateTimeZone
        )
    }

    /**
     * Converts date time from java8 to joda DateTime
     */
    fun toJodaDateTime(
            javaLocalDateTime: java.time.LocalDateTime
    ): DateTime {
        return DateTime(
                javaLocalDateTime.year,
                javaLocalDateTime.monthValue,
                javaLocalDateTime.dayOfMonth,
                javaLocalDateTime.hour,
                javaLocalDateTime.minute,
                javaLocalDateTime.second,
                dateTimeZone
        )
    }

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
         * Converts joda [DateTime] to java8 [LocalDate]
         */
        fun toJavaLocalTime(jodaDateTime: DateTime): java.time.LocalTime {
            return java.time.LocalTime.of(
                    jodaDateTime.hourOfDay,
                    jodaDateTime.minuteOfHour,
                    jodaDateTime.secondOfMinute
            )
        }

        /**
         * Converts java8 [LocalDate] to joda [DateTime]
         */
        fun toJodaDate(javaLocalDate: java.time.LocalDate): LocalDate {
            return LocalDate(
                    javaLocalDate.year,
                    javaLocalDate.monthValue,
                    javaLocalDate.dayOfMonth
            )
        }

    }

}
