package lt.markmerkk.utils

import org.joda.time.format.DateTimeFormat
import lt.markmerkk.entities.Log
import org.joda.time.*
import org.joda.time.format.PeriodFormatterBuilder
import org.slf4j.LoggerFactory

object LogFormatters {
    private val l = LoggerFactory.getLogger(LogFormatters::class.java)!!
    const val TIME_SHORT_FORMAT = "HH:mm"
    const val DATE_SHORT_FORMAT = "yyyy-MM-dd"
    const val DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm"
    const val DATE_VERY_LONG_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"

    val formatTime = DateTimeFormat.forPattern(TIME_SHORT_FORMAT)!!
    val formatDate = DateTimeFormat.forPattern(DATE_SHORT_FORMAT)!!
    val longFormatDateTime = DateTimeFormat.forPattern(DATE_LONG_FORMAT)!!
    val veryLongFormat = DateTimeFormat.forPattern(DATE_VERY_LONG_FORMAT)!!

    val defaultDate = LocalDate(1970, 1, 1)

    private val periodFormatter = PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix("d")
            .appendHours()
            .appendSuffix("h")
            .appendMinutes()
            .appendSuffix("m")
            .appendSeconds()
            .appendSuffix("s")
            .toFormatter()

    private val periodFormatterShort = PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix("d")
            .appendHours()
            .appendSuffix("h")
            .appendMinutes()
            .appendSuffix("m")
            .appendSeconds()
            .appendSuffix("s")
            .toFormatter()

    fun humanReadableDuration(duration: Duration): String {
        return periodFormatter.print(duration.toPeriod())
    }

    fun humanReadableDurationShort(duration: Duration): String {
        if (duration.standardMinutes <= 0)
            return "0m"
        val builder = StringBuilder()
        val type = PeriodType.forFields(arrayOf(DurationFieldType.hours(), DurationFieldType.minutes()))
        val period = Period(duration, type)
        if (period.days != 0)
            builder.append(period.days).append("d").append(" ")
        if (period.hours != 0)
            builder.append(period.hours).append("h").append(" ")
        if (period.minutes != 0)
            builder.append(period.minutes).append("m").append(" ")
        if (builder.isNotEmpty() && builder[builder.length - 1] == " "[0])
            builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    fun formatLogsBasic(logs: List<Log>): String {
        val hasMultipleDates = hasMultipleDates(logs)
        return logs
                .map { formatLogBasic(log = it, includeDate = hasMultipleDates) }
                .joinToString("\n")
    }

    fun formatLogBasic(log: Log, includeDate: Boolean): String {
        val startDate = LogFormatters.formatDate.print(log.time.start)
        val formatStart = LogFormatters.formatTime.print(log.time.start)
        val formatEnd = LogFormatters.formatTime.print(log.time.end)
        val durationAsString = LogFormatters.humanReadableDurationShort(log.time.duration)
        return StringBuilder()
                .append(if (includeDate) "$startDate " else "")
                .append("$formatStart - $formatEnd ($durationAsString)")
                .append(" >> ")
                .append(if (!log.code.isEmpty()) "'${log.code.code}' " else "")
                .append("'${log.comment}'")
                .toString()
    }

    /**
     * @return logs are in span through multiple dates
     */
    fun hasMultipleDates(logs: List<Log>): Boolean {
        val logDates: Set<LocalDate> = logs
                .map { it.time.start.toLocalDate() }
                .toSet()
        return logDates.size > 1
    }

    fun dtFromRawOrNull(
        dateAsString: String,
        timeAsString: String
    ): DateTime? {
        return try {
            val date = LogFormatters.formatDate.parseLocalDate(dateAsString)
            val time = LogFormatters.formatTime.parseLocalTime(timeAsString)
            date.toDateTime(time)
        } catch (e: IllegalArgumentException) {
            l.warn("Error parsing date time as string from ${dateAsString} / ${timeAsString}", e)
            null
        }
    }

    fun dateFromRawOrDefault(
        dateAsString: String
    ): LocalDate {
        return try {
            formatDate.parseLocalDate(dateAsString)
        } catch (e: IllegalArgumentException) {
            l.warn("Error parsing date as string from ${dateAsString}", e)
            defaultDate
        }
    }

    fun timeFromRawOrDefault(
        timeAsString: String
    ): LocalTime {
        return try {
            formatTime.parseLocalTime(timeAsString)
        } catch (e: IllegalArgumentException) {
            l.warn("Error parsing time as string from ${timeAsString}", e)
            LocalTime.MIDNIGHT
        }
    }

    /**
     * Formats time
     * If not the same day, will include date
     */
    fun formatTime(dtCurrent: DateTime, dtTarget: DateTime): String {
        return if (dtCurrent.toLocalDate().isEqual(dtTarget.toLocalDate())) {
            formatTime.print(dtTarget)
        } else {
            longFormatDateTime.print(dtTarget)
        }
    }
}

fun Duration.toStringShort(): String {
    return LogFormatters.humanReadableDurationShort(this)
}