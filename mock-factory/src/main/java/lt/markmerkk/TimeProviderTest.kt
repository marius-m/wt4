package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeZone
import java.time.LocalDateTime
import java.time.ZoneId

open class TimeProviderTest: TimeProvider {

    override val dateTimeZone: DateTimeZone = DateTimeZone.UTC
    override val zoneId: ZoneId = ZoneId.of(dateTimeZone.id)

    private val realTimeProvider = TimeProviderJfx(dateTimeZone)

    init {
        DateTimeUtils.setCurrentMillisFixed(1L)
    }

    override fun now(): DateTime = DateTime.now()

    override fun jNow(): LocalDateTime = jLocalDateTimeFrom(now().millis)

    override fun jLocalDateTimeFrom(millis: Long): LocalDateTime {
        return realTimeProvider.jLocalDateTimeFrom(millis)
    }

    override fun jMillisFrom(dateTime: LocalDateTime): Long {
        return realTimeProvider.jMillisFrom(dateTime)
    }

    override fun dateTimeFromMillis(millis: Long): DateTime {
        return DateTime(millis, dateTimeZone)
    }

    override fun millisFrom(dateTime: DateTime): Long {
        return dateTime.withZone(dateTimeZone)
                .millis
    }

}