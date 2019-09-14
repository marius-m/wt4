package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * All things related date ant time
 * Note: All methods returning java8 time will have prefix 'j'
 */
class TimeProviderJfx(
        override val dateTimeZone: DateTimeZone = DateTimeZone.getDefault()
): TimeProvider {

    override val zoneId: java.time.ZoneId = java.time.ZoneId.of(dateTimeZone.id)

    override fun now(): DateTime = DateTime.now(dateTimeZone)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0)

    override fun nowMillis(): Long = roundMillis(now())

    override fun jNow(): java.time.LocalDateTime {
        val nowMillis = now().millis
        return roundDateTimeJava8(nowMillis)
    }

    //region Convenience

    override fun roundDateTime(millis: Long): DateTime {
        return DateTime(millis, dateTimeZone)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
    }

    override fun roundDateTimeJava8(millis: Long): java.time.LocalDateTime {
        return java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(millis),
                zoneId
        ).withSecond(0)
                .withNano(0)
    }

    override fun roundMillis(dateTime: DateTime): Long {
        return dateTime.withZone(dateTimeZone)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .millis
    }

    //endregion

}