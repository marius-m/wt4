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

    override fun preciseNow(): DateTime = DateTime
            .now(dateTimeZone)

    override fun preciseNowMillis(): Long = DateTime
            .now(dateTimeZone)
            .millis

    override fun now(): DateTime = DateTime.now(dateTimeZone)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0)

    override fun nowMillis(): Long = now().roundMillis()

    override fun jNow(): LocalDateTime = roundDateTimeJava8(now().millis)

    override fun roundDateTimeJava8(millis: Long): LocalDateTime {
        return realTimeProvider.roundDateTimeJava8(millis)
                .withSecond(0)
                .withNano(0)
    }

    override fun preciseDateTime(millis: Long): DateTime {
        return DateTime(millis, dateTimeZone)
    }

    override fun roundMillisToDt(millis: Long): DateTime {
        return DateTime(millis, dateTimeZone)
    }

    override fun preciseMillis(dateTime: DateTime): Long {
        return dateTime.withZone(dateTimeZone)
                .millis
    }
}