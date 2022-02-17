package lt.markmerkk.utils

import lt.markmerkk.entities.Log
import org.joda.time.*

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
     * Formats duration time into pretty and short string format
     * @param duration provided duration to format
     * *
     * @return formatted duration
     */
    fun formatShortDuration(duration: Duration): String {
        return LogFormatters.humanReadableDurationShort(duration)
    }

    /**
     * Formats duration time into pretty and short string format
     * @param durationMillis provided duration to format
     * *
     * @return formatted duration
     */
    fun formatShortDurationMillis(durationMillis: Long): String {
        val duration = Duration(durationMillis)
        return LogFormatters.humanReadableDurationShort(duration)
    }

    fun firstLine(input: String): String {
        return input.split("\n")
                .first()
    }

    /**
     * Formats log as a pretty text
     */
    @JvmStatic fun formatLogToText(log: Log): String {
        val timeFrom = LogFormatters.shortFormat.print(log.time.start)
        val timeTo = LogFormatters.shortFormat.print(log.time.end)
        val duration = formatShortDurationMillis(log.time.duration.millis)
        return "${log.code.code} ($timeFrom - $timeTo = $duration) ${firstLine(log.comment)}"
                .trim()
    }

}