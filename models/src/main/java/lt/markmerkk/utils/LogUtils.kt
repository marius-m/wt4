package lt.markmerkk.utils

import lt.markmerkk.entities.SimpleLog
import org.joda.time.DurationFieldType
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.regex.Pattern

object LogUtils {

    const val NO_NUMBER = -1
    private val ticketRegex = "([a-zA-Z0-9]+)-([0-9]+)".toRegex()

    /**
     * Inspects id for a valid type
     * @param message
     */
    fun validateTaskTitle(message: String): String {
        val result = ticketRegex.find(message) ?: return ""
        return result
                .groupValues
                .firstOrNull()
                ?.toUpperCase() ?: ""
    }

    /**
     * Splits task title and returns it
     */
    fun splitTaskTitle(message: String): String {
        val result = ticketRegex.find(message) ?: return ""
        if (result.groupValues.size > 1) {
            return result.groupValues[1]
                    .toUpperCase()
        }
        return ""
    }

    /**
     * Splits task title into
     */
    fun splitTaskNumber(message: String): Int {
        val ticketMatch: MatchResult = ticketRegex.find(message) ?: return NO_NUMBER
        if (ticketMatch.groupValues.size > 1)
            return ticketMatch.groupValues[2].toInt()
        return NO_NUMBER
    }

    /**
     * Formats duration time into pretty string format
     * @param durationMillis provided duration to format
     * *
     * @return formatted duration
     */
    fun formatDuration(durationMillis: Long): String {
        if (durationMillis < 1000)
            return "0s"
        val builder = StringBuilder()
        val type = PeriodType.forFields(arrayOf(DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds()))

        val period = Period(durationMillis, type)
        if (period.days != 0)
            builder.append(period.days).append("d").append(" ")
        if (period.hours != 0)
            builder.append(period.hours).append("h").append(" ")
        if (period.minutes != 0)
            builder.append(period.minutes).append("m").append(" ")
        if (period.seconds != 0)
            builder.append(period.seconds).append("s").append(" ")
        if (builder.length > 0 && builder[builder.length - 1] == " "[0])
            builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    /**
     * Formats duration time into pretty and short string format
     * @param durationMillis provided duration to format
     * *
     * @return formatted duration
     */
    // fixme : needs tests, as this code was copied from earlier project
    fun formatShortDuration(durationMillis: Long): String {
        if (durationMillis < 1000 * 60)
            return "0m"
        val builder = StringBuilder()
        val type = PeriodType.forFields(arrayOf(DurationFieldType.hours(), DurationFieldType.minutes()))

        val period = Period(durationMillis, type)
        if (period.days != 0)
            builder.append(period.days).append("d").append(" ")
        if (period.hours != 0)
            builder.append(period.hours).append("h").append(" ")
        if (period.minutes != 0)
            builder.append(period.minutes).append("m").append(" ")
        if (builder.length > 0 && builder[builder.length - 1] == " "[0])
            builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    fun firstLine(input: String): String {
        return input.split("\n")
                .first()
    }

    /**
     * Formats log as a pretty text
     */
    @JvmStatic fun formatLogToText(simpleLog: SimpleLog): String {
        val timeFrom = LogFormatters.shortFormat.print(simpleLog.start)
        val timeTo = LogFormatters.shortFormat.print(simpleLog.end)
        val duration = formatShortDuration(simpleLog.duration)
        return "${simpleLog.task} ($timeFrom - $timeTo = $duration) ${firstLine(simpleLog.comment)}"
                .trim()
    }

}