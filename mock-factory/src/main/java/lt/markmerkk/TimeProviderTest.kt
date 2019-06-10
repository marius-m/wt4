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

    override fun jNow(): LocalDateTime = roundDateTimeJava8(now().millis)

    override fun roundDateTimeJava8(millis: Long): LocalDateTime {
        return realTimeProvider.roundDateTimeJava8(millis)
                .withSecond(0)
                .withNano(0)
    }

    override fun roundDateTime(millis: Long): DateTime {
        return DateTime(millis, dateTimeZone)
    }

    override fun roundMillis(dateTime: DateTime): Long {
        return dateTime.withZone(dateTimeZone)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .millis
    }

}