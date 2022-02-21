package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate

interface TimeProvider {
    val dateTimeZone: DateTimeZone
    val zoneId: java.time.ZoneId

    fun preciseNow(): DateTime

    fun preciseNowMillis(): Long

    /**
     * @return instance of 'now' in joda datetime
     */
    fun now(): DateTime

    fun nowMillis(): Long

    /**
     * @return instance of 'now' in java8 datetime
     */
    fun jNow(): java.time.LocalDateTime

    fun preciseDateTime(millis: Long): DateTime

    /**
     * Converts millis to joda datetime
     */
    fun roundMillisToDt(millis: Long): DateTime

    fun preciseMillis(dateTime: DateTime): Long

    /**
     * Converts millis to java8 datetime
     */
    fun roundDateTimeJava8(millis: Long): java.time.LocalDateTime

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
        ).withSecondOfMinute(0)
                .withMillisOfSecond(0)
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
        fun toJavaLocalDate(jodaDate: LocalDate): java.time.LocalDate {
            return java.time.LocalDate.of(
                jodaDate.year,
                jodaDate.monthOfYear,
                jodaDate.dayOfMonth
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

/**
 * Rounds time to minutes
 */
fun DateTime.round(): DateTime {
    return this.withSecondOfMinute(0)
        .withMillisOfSecond(0)
}
/**
 * Rounds time to minutes
 */
fun DateTime.roundMillis(): Long {
    return this.round().millis
}
