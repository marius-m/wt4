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

    override fun now(): DateTime = DateTime.now()

    override fun jNow(): java.time.LocalDateTime {
        val nowMillis = now().millis
        return jLocalDateTimeFrom(nowMillis)
    }

    //region Convenience

    override fun jLocalDateTimeFrom(millis: Long): java.time.LocalDateTime {
        return java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(millis),
                zoneId
        )
    }

    override fun jMillisFrom(dateTime: java.time.LocalDateTime): Long {
        return dateTime.atZone(zoneId).toInstant().toEpochMilli()
    }

    //endregion

}